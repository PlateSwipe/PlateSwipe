package com.android.sample.model.user

import android.util.Log
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_EXPIRATION_DATE
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_EXPIRATION_DATE_DAY
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_EXPIRATION_DATE_MONTH
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_EXPIRATION_DATE_YEAR
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_ID
import com.android.sample.resources.C.Tag.UserRepositoryFirestore.FRIDGE_FIELD_QUANTITY
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class UserRepositoryFirestore(private val db: FirebaseFirestore) : UserRepository {
  private val collectionPath = "users"

  override fun init(onSuccess: () -> Unit) {
    if (Firebase.auth.currentUser != null) {
      onSuccess()
    }
  }

  override fun getUserById(id: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
    val doc = db.collection(collectionPath).document(id)
    doc.get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val user = convertSnapshot(id, task.result)
        if (user != null) {
          onSuccess(user)
        } else {
          onFailure(Exception("Error converting snapshot to user"))
        }
      } else {
        task.exception?.let { onFailure(it) }
      }
    }
  }

  override fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performUserDatabaseAction(
        db.collection(collectionPath).document(user.uid).set(user), onSuccess, onFailure)
  }

  override fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performUserDatabaseAction(
        db.collection(collectionPath).document(user.uid).set(user), onSuccess, onFailure)
  }

  override fun deleteUserById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performUserDatabaseAction(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  /**
   * Method that allows us to convert DocumentSnapshots into User data classes
   *
   * @param uid unique id of the user of which the snapshot represents
   * @param snapshot DocumentSnapshot that was extracted from Firestore and which we want to convert
   *   into a User
   * @return a [User] data class that is created from the DocumentSnapshot if there is not problem
   *   otherwise it logs an error and returns null
   */
  @Suppress("UNCHECKED_CAST")
  private fun convertSnapshot(uid: String, snapshot: DocumentSnapshot): User? {
    return try {
      val userName = snapshot["userName"] as String
      val profilePictureUrl = snapshot["profilePictureUrl"] as String
      val fridge =
          (snapshot["fridge"] as List<Map<String, Any>>).map { mapping ->
            fridgeItemExtraction(mapping)
          }
      val likedRecipes = snapshot["likedRecipes"] as List<String>
      val createdRecipes = snapshot["createdRecipes"] as List<String>
      val dateOfBirth = snapshot["dateOfBirth"] as String

      User(uid, userName, profilePictureUrl, fridge, likedRecipes, createdRecipes, dateOfBirth)
    } catch (e: Exception) {
      Log.e("UserRepositoryFirestore", "Error converting snapshot to user", e)
      null
    }
  }

  /**
   * Method that transform the mapping of an FridgeItem from the database to the object [FridgeItem]
   *
   * @param mapping the mapping extracted from the database for a FridgeItem
   * @return an object FridgeItem
   */
  @Suppress("UNCHECKED_CAST")
  private fun fridgeItemExtraction(mapping: Map<String, Any>): FridgeItem {
    val id = mapping[FRIDGE_FIELD_ID] as String
    val quantity = (mapping[FRIDGE_FIELD_QUANTITY] as Long).toInt()
    val dateMap = mapping[FRIDGE_FIELD_EXPIRATION_DATE] as Map<String, Any>
    val date =
        LocalDate.of(
            (dateMap[FRIDGE_FIELD_EXPIRATION_DATE_YEAR] as Long).toInt(),
            (dateMap[FRIDGE_FIELD_EXPIRATION_DATE_MONTH] as Long).toInt(),
            (dateMap[FRIDGE_FIELD_EXPIRATION_DATE_DAY] as Long).toInt())
    return FridgeItem(id, quantity, date)
  }

  /**
   * Method that allows us to perform a Firestore database call
   *
   * @param task the task or call that will be performed
   * @param onSuccess function that will be called if the task is successful
   * @param onFailure function that will be called if the task fails
   */
  private fun performUserDatabaseAction(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("UserRepositoryFirestore", "Error performing user database action", e)
          onFailure(e)
        }
      }
    }
  }
}
