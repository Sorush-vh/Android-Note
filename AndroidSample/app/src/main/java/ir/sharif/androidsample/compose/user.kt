package ir.sharif.androidsample.compose

import android.location.Address
import androidx.compose.runtime.mutableStateListOf

public val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")


data class User(
  var firstName: String,
  var lastName: String,
  var username: String,
  var emailAddress: String,
  var password: String,
){
  init {
    require(firstName.isNotBlank()) { "First name is required." }
    require(lastName.isNotBlank())  { "Last name is required." }
    require(username.isNotBlank())  { "Username is required." }
    require(emailAddress.isNotBlank()) { "Email is required." }
    require(emailAddress.matches(EMAIL_REGEX)) { "Invalid email address format." }
    require(password.length >= 6) { "Password must be at least 6 characters." }
  }
  companion object {
    val users = mutableListOf<Task>()
    fun create(
      firstName: String,
      lastName: String,
      username: String,
      emailAddress: String,
      password: String,
      repeatPassword: String
    ): User {
      require(password == repeatPassword) { "Passwords do not match." }
      return User(
        firstName.trim(),
        lastName.trim(),
        username.trim(),
        emailAddress.trim(),
        password
      )
    }
  }

}
