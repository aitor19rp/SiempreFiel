package com.aitorramos.siemprefiel.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitorramos.siemprefiel.R
import com.aitorramos.siemprefiel.adapters.DogAdapter
import com.aitorramos.siemprefiel.app.preferences
import com.aitorramos.siemprefiel.listeners.RecyclerDogListener
import com.aitorramos.siemprefiel.models.Dog
import com.aitorramos.siemprefiel.toast
import com.aitorramos.siemprefiel.utils.CircleTransform
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_add_dog.view.*
import kotlinx.android.synthetic.main.fragment_morning.view.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class DogsFragment : Fragment() {

    private lateinit var _view: View
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var dogDb: CollectionReference
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var adapter: DogAdapter
    private var dogList: ArrayList<Dog> = ArrayList()
    lateinit var dialogView: View//
    var imageflag = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_dogs, container, false)
        if(preferences!!.range == 1){
            setHasOptionsMenu(true)
        }

        setDogDb()
        setUpRecyclerView()

        return _view
    }

    private fun setDogDb() {
        dogDb = store.collection("dogs")
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()

        adapter = DogAdapter(dogList, object: RecyclerDogListener {
            override fun onClick(dogs: Dog, position: Int) {

            }

            override fun onDelete(dogs: Dog, position: Int) {
                deleteDog(dogs.url, dogs.name)
            }

            override fun onSelect(dogs: Dog, position: Int, status: Boolean) {

            }

        }, "0")

        _view.recyclerView.adapter = adapter
    }

    private fun getDogs(){
        dogList.clear()
        dogDb
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    var dog = Dog()

                    dog.url = document.getString("url").toString()
                    dog.name = document.getString("name").toString()

                    dogList.add(dog)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                activity!!.toast("Error al obtener los datos")
            }
    }

    private fun setAddDogDialog(){

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_dog, null)
        val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Añadir perro")
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar"){_, _ ->

            }
        dialogView.ivPhoto.setOnClickListener{
            openGalleryForImage()
        }
        val dialog = builder.create()
        dialog.setOnShowListener{
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener{
                if(imageflag == false || dialogView.etName.text.toString() == ""){
                    activity!!.toast("Se debe añadir foto y nombre")
                }else{
                    uploadImage(dialogView.etName.text.toString())
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

    }

    private fun deleteDog(url: String, name: String){
        dogDb
            .whereEqualTo("url", url)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    dogDb.document(document.id).delete()
                        .addOnSuccessListener {
                            getDogs()
                            Snackbar.make(activity!!.findViewById(R.id.drawerLayout),"$name borrado/a", Snackbar.LENGTH_LONG).setAction("Deshacer", View.OnClickListener {
                                addDog(url, name)
                            }).show()
                        }
                }
            }.addOnFailureListener{
                activity!!.toast("Error al borrar")
            }
    }

    private fun uploadImage(name: String) {
        val ref = storageReference.child("dogs/$name.jpeg")
        dialogView.ivPhoto.isDrawingCacheEnabled = true
        dialogView.ivPhoto.buildDrawingCache()
        val bitmap = (dialogView.ivPhoto.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = ref.putBytes(data)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                addDog("$it", name)
            }.addOnFailureListener() {
                activity!!.toast("Error al subir imagen")
            }
        }
    }

    private fun addDog(url: String, name: String){
        val dog = hashMapOf(
            "url" to url,
            "name" to name.capitalize()
        )

        dogDb.add(dog)
            .addOnSuccessListener {
                getDogs()
            }.addOnFailureListener{
                activity!!.toast("Error al añadir")
            }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            Picasso.get()
                .load(data?.data)
                .resize(100, 100)
                .centerCrop()
                .transform(CircleTransform())
                .into(dialogView.ivPhoto)

            imageflag = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_dogs, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.addDog ->{
                setAddDogDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        getDogs()
    }
}