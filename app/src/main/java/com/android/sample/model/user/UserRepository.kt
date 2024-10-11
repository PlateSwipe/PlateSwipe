package com.android.sample.model.user

interface UserRepository {
    /**
     * Initializes the database
     * @param onSuccess function called if the operation is successful
     */
    fun init(onSuccess: () -> Unit)

    /**
     * Retrieves a specific user from the database using its id
     * @param id id of the user that we want to retrieve from the database
     * @param onSuccess function called if the operation is successful
     * @param onFailure function called if the operation is not successful
     */
    fun getUserById(id: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Adds a new user in the database
     * @param user user that we want to add to the database
     * @param onSuccess function called if the operation is successful
     * @param onFailure function called if the operation is not successful
     */
    fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates the data of a specific user in the database
     * @param user user that we want to update in the database
     * @param onSuccess function called if the operation is successful
     * @param onFailure function called if the operation is not successful
     */
    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Deletes a specific user from the database using its id
     * @param id id of the user that we want to delete from the database
     * @param onSuccess function called if the operation is successful
     * @param onFailure function called if the operation is not successful
     */
    fun deleteUserById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}