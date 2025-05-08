package com.edu.cit.hkotisk

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.cit.hkotisk.data.api.AuthService
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.UpdateProfileRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    companion object {
        fun createIntent(context: android.content.Context) = android.content.Intent(context, ProfileActivity::class.java)
    }

    private lateinit var authService: AuthService
    private lateinit var loadingSpinner: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        authService = RetrofitClient.createAuthService(this)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_profile

        // Initialize profile views
        val nameTextView = findViewById<TextView>(R.id.name_value)
        val emailTextView = findViewById<TextView>(R.id.email_value)
        val editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        loadingSpinner = findViewById(R.id.loadingSpinner)

        // Load user profile
        loadUserProfile(nameTextView, emailTextView)

        editProfileButton.setOnClickListener {
            showEditProfileDialog(nameTextView.text.toString())
        }

        logoutButton.setOnClickListener {
            // Clear stored token from SharedPreferences
            val sharedPrefs = getSharedPreferences("secure_prefs", MODE_PRIVATE)
            sharedPrefs.edit().remove("auth_token").apply()
            
            // Navigate to LandingPage
            startActivity(Intent(this, LandingPage::class.java))
            finishAffinity() // Close all activities in the stack
        }
        
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    startActivity(Dashboard.createIntent(this))
                    finish()
                    true
                }
                R.id.navigation_orders -> {
                    startActivity(OrdersActivity.createIntent(this))
                    finish()
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(CartActivity.createIntent(this))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserProfile(nameTextView: TextView, emailTextView: TextView) {
        lifecycleScope.launch {
            loadingSpinner.visibility = android.view.View.VISIBLE
            try {
                val response = authService.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        nameTextView.text = profile.username
                        emailTextView.text = profile.email
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingSpinner.visibility = android.view.View.GONE
            }
        }
    }

    private fun showEditProfileDialog(currentUsername: String) {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        dialog.setContentView(view)

        val usernameInput = view.findViewById<TextInputEditText>(R.id.username_input)
        val currentPasswordInput = view.findViewById<TextInputEditText>(R.id.current_password_input)
        val newPasswordInput = view.findViewById<TextInputEditText>(R.id.new_password_input)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.confirm_password_input)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        usernameInput.setText(currentUsername)

        saveButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val currentPassword = currentPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (username.isBlank()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentPassword.isBlank()) {
                Toast.makeText(this, "Current password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isBlank()) {
                Toast.makeText(this, "New password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPassword.isBlank()) {
                Toast.makeText(this, "Confirm password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(
                username,
                currentPassword,
                newPassword,
                dialog
            )
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateProfile(
        username: String,
        currentPassword: String?,
        newPassword: String?,
        dialog: Dialog
    ) {
        lifecycleScope.launch {
            loadingSpinner.visibility = android.view.View.VISIBLE
            try {
                if (username.isBlank()) {
                    Toast.makeText(this@ProfileActivity, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (newPassword.isNullOrBlank()) {
                    Toast.makeText(this@ProfileActivity, "New password is required", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (currentPassword.isNullOrBlank()) {
                    Toast.makeText(this@ProfileActivity, "Current password is required", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val emailValue = findViewById<TextView>(R.id.email_value).text.toString()
                val response = authService.updateProfile(
                    UpdateProfileRequest(
                        email = emailValue,
                        username = username,
                        password = newPassword
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        findViewById<TextView>(R.id.name_value).text = profile.username
                        findViewById<TextView>(R.id.email_value).text = profile.email
                        Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingSpinner.visibility = android.view.View.GONE
            }
        }
    }
}
