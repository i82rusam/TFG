package com.example.tfg.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditUserActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etNewPassword: EditText
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        btnSave = findViewById(R.id.btnSave)
        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etNewPassword = findViewById(R.id.etNewPassword)
        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val imageView = findViewById<ImageView>(R.id.ivProfilePicture)

        loadUserData()

        btnSelectImage.setOnClickListener {
            selectImage()
        }

        btnSave.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            if (newPassword.isNotEmpty()) {
                updatePassword(newPassword)
            }
            saveUserData()
        }

        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { userId ->
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        val email = document.getString("email") ?: ""
                        val username = document.getString("username") ?: ""
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""
                        etName.setText(name)
                        etLastName.setText(lastName)
                        etEmail.setText(email)
                        etUsername.setText(username)
                        // Cargar la imagen de perfil usando Glide o tu librería de carga de imágenes preferida
                        Glide.with(this).load(profileImageUrl).into(findViewById(R.id.ivProfilePicture))
                    } else {
                        Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickCode)
    }

    private fun updatePassword(newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        val newPassword = etNewPassword.text.toString().trim()

        // Actualizar datos del usuario excepto la contraseña
        val userData = hashMapOf(
            "name" to etName.text.toString(),
            "lastName" to etLastName.text.toString(),
            "email" to etEmail.text.toString(),
            "username" to etUsername.text.toString()
            // No incluir la contraseña aquí
        )

        user?.uid?.let { userId ->
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .update(userData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos del usuario actualizados con éxito", Toast.LENGTH_SHORT).show()
                    // Actualizar la contraseña solo si se ha proporcionado una nueva
                    if (newPassword.isNotEmpty()) {
                        user.updatePassword(newPassword).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    // Navegar a ProfileActivity después de actualizar los datos
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar los datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imagePickCode) {
            val imageUri = data?.data
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    Glide.with(this).load(uri).into(findViewById(R.id.ivProfilePicture))
                    // Guardar la URL de la imagen en Firestore
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.uid?.let { userId ->
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .update("profileImageUrl", imageUrl)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Imagen de perfil actualizada con éxito", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar la imagen de perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
    }

    companion object {
        private const val imagePickCode = 1000
    }
}