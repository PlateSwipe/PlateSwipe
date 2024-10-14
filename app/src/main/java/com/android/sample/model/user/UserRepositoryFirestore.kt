package com.android.sample.model.user

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

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
        onSuccess(convertSnapshot(id, task.result))
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
   * @return a [User] data class that is created from the DocumentSnapshot
   */
  @Suppress("UNCHECKED_CAST")
  private fun convertSnapshot(uid: String, snapshot: DocumentSnapshot): User {
    val userName = snapshot["userName"] as String
    val profilePictureUrl = snapshot["profilePictureUrl"] as String
    val fridge = snapshot["fridge"] as List<String>
    val savedRecipes = snapshot["savedRecipes"] as List<String>
    val createdRecipes = snapshot["createdRecipes"] as List<String>
    return User(uid, userName, profilePictureUrl, fridge, savedRecipes, createdRecipes)
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
