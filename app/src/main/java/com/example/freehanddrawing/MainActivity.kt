package com.example.freehanddrawing

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var drawingView: DrwaingView?=null

    private var mImageButtonCurrentPaint: ImageButton?=null

    val openGalleryLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){  //Allows us to execute what we wants to run and gives result at the same time
                result->
                if(result.resultCode==RESULT_OK && result.data!=null){  //I want to take that data that is given to me from that result and then I want to assign it to my image view (iv_background)
                    val imageBackGround: ImageView=findViewById(R.id.iv_background)
                    imageBackGround.setImageURI(result.data?.data)  // URI means the path of image, here we are not copying image just directly putting its address
                }
            }

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val perMissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission granted now you can read the storage files.",
                        Toast.LENGTH_LONG
                    ).show()
                    val pickIntent= Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    openGalleryLauncher.launch(pickIntent)
                } else {
                    if (perMissionName == Manifest.permission.READ_EXTERNAL_STORAGE)
                        Toast.makeText(
                            this@MainActivity,
                            "Oops you just denied the permission.",
                            Toast.LENGTH_LONG
                        ).show()
                }
            }

        }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(10.toFloat())

        val linearLayoutPaintcolors=findViewById<LinearLayout>(R.id.paint_colors)
        mImageButtonCurrentPaint=linearLayoutPaintcolors[2] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        val ib_brush_btn: ImageButton=findViewById(R.id.ib_brush)
        ib_brush_btn.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        val ibGallery: ImageButton=findViewById(R.id.ib_img_gal)
        ibGallery.setOnClickListener{
            requestStoragePermission()
        }
    }

    private fun showBrushSizeChooserDialog(){
        var brushDialog= Dialog(this) //brushDialog will appear as popup
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn: ImageButton=brushDialog.findViewById(R.id.ib_small_brush)
        val mediumBtn: ImageButton=brushDialog.findViewById(R.id.ib_medium_brush)
        val largeBtn: ImageButton=brushDialog.findViewById(R.id.ib_large_brush)

        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    fun paintClicked(view: View){
        if(view!= mImageButtonCurrentPaint){
            val imageButton =view as ImageButton
            val colorTag=imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton!!.setImageDrawable(  //Button we presses
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint?.setImageDrawable( //rest of all the buttons
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )

            mImageButtonCurrentPaint=view
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_MEDIA_IMAGES)
            ){
            showRationaleDialog("Kids Drawing App", "Kids Drawing App"+
            "needs to Access Your External Storage")
        }else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES

            ))
        }
    }
    private fun showRationaleDialog(
        title: String,
        message: String
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}


