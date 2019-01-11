package com.a9itgroup.crossstitchart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_with_nav.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.cross_stitch.*
import petrov.kristiyan.colorpicker.ColorPicker
import android.app.Activity
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.view.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity(), CrossStitch.CrossStitchListener, CrossView.SquarePressedListener,
    NavigationView.OnNavigationItemSelectedListener {
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showSaveDialog("txt")
                }
                return
            }
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showSaveDialog("jpeg")
                }
                return
            }
            3 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showSaveDialog("png")
                }
                return
            }
        }
    }

    private fun getFilePathFromUri(uri: Uri): String{
        val path = RealPathUtil.getRealPath(this, uri)
        if (path!= null)
            return path
        return ""
    }

    private fun saveCSAFile(filename: String, folder: String){
        val file = File(folder, "$filename.csa")
        val fos = FileOutputStream(file)
//            openFileOutput("$filename.txt", Context.MODE_PRIVATE).use {
//                crossView.saveToFile(it)
//            }
        crossView.saveToFile(fos)
        fos.close()
    }

    private fun exportFile(_fileName: String, folder: String, fileType: String){
        Log.d("ExportFile","Export to "+ folder +File.separator+"$_fileName.$fileType")
        var fileName = _fileName
        if (!_fileName.endsWith(".csa",true)) fileName += ".csa"
        val file = File(folder, fileName)
        val fos = FileOutputStream(file)
//            openFileOutput("$_fileName.txt", Context.MODE_PRIVATE).use {
//                crossView.saveToFile(it)
//            }
        crossView.exportFile(fos, fileType)
        fos.close()
    }

    private fun showSaveDialog(saveType: String){
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            return

        var filename = "new"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter file name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            if (input.text.isNotEmpty())
                filename = input.text.toString()


            val root = Environment.getExternalStorageDirectory().toString()
            Log.d("FileName", root)
            val folder = File(root+File.separator+"CrossStitchProjects")
            if (!folder.exists()){
                folder.mkdirs()
            }
            if (folder.exists()){
//            val path = "$root/$filename.txt"
                when (saveType){
                    "txt" -> saveCSAFile(filename, folder.toString())
                    else -> exportFile(filename, folder.toString(),saveType)
                }
            }
        }

        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_new -> {
                crossView.new()
                zoom_layout.zoomTo(1.0f, true)
            }
            R.id.nav_save -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    showSaveDialog("txt")
                }
            }
            R.id.nav_load -> {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
            }
            R.id.nav_quit -> {
                finish()
            }
            R.id.nav_export_jpeg -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                } else {
                    showSaveDialog("jpeg")
                }
            }
            R.id.nav_export_png -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
                } else {
                    showSaveDialog("png")
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val selectedFile = data!!.data //The uri with the location of the file
            if (selectedFile!=null){
                val path = getFilePathFromUri(selectedFile)
                Log.d("OpenFilePath", path)
                if (path.isNotEmpty() and path.endsWith(".csa",true))
                    crossView.readFromFile(path)
                else
                    Toast.makeText(this,"Not a CSA file for Cross Stitch Art!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var crossStitch: CrossStitch
    private lateinit var colorData: ArrayList<MyColor>

    override fun onSquarePressed(i: Int, j: Int) {
        crossStitch.moveAt(i, j)
    }

    override fun movedAt(x: Int, y: Int, move: Int) {
        crossView.toggleColorAtPosition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_with_nav)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val colorPicker = ColorPicker(this)

            val colors = ArrayList<String>()
            //SILVER //GRAY //BLACK //RED
            colors.add("#C0C0C0")
            colors.add("#808080")
            colors.add("#000000")
            colors.add("#FF0000")
            //MAROON //YELLOW //OLIVE //LIME
            colors.add("#800000")
            colors.add("#FFFF00")
            colors.add("#808000")
            colors.add("#00FF00")
            //GREEN //AQUA //TEAL //BLUE
            colors.add("#008000")
            colors.add("#00FFFF")
            colors.add("#008080")
            colors.add("#0000FF")
            // NAVY //FUCHSIA //PURPLE //ORANGE
            colors.add("#000080")
            colors.add("#FF00FF")
            colors.add("#800080")
            colors.add("#FFA500")


            colorPicker
                .setColors(colors)
                .disableDefaultButtons(true)
//                .setDefaultColorButton(Color.parseColor("#C0C0C0"))
                .setColumns(4)
                .setRoundColorButton(true)
                .setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                    override fun setOnFastChooseColorListener(position: Int, color: Int) {
                        Log.d("ColorChoose",color.toString())
                        fab.backgroundTintList = ColorStateList.valueOf(color)
                        crossView.changeColor(color)
                        colorPicker.dismissDialog()
                    }
                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "onCancel", Toast.LENGTH_SHORT).show()
                    }
                })
                .show()
        }

        fab_zoom_in.setOnClickListener {
            zoom_layout.zoomIn()
        }

        fab_zoom_out.setOnClickListener {
            zoom_layout.zoomOut()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
//        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toggle.syncState()

//        toolbar.setNavigationOnClickListener {
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//            drawer_layout.openDrawer(GravityCompat.START)
//        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.pixel_counter -> {
                colorData = crossView.getColorData()

                createPixelCounterPopup()

                return true
            }
            R.id.fragment_config -> {
                showConfigScreen()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfigScreen() {
        cross_stitch_in_activity_main.visibility = View.GONE

        container.visibility = View.VISIBLE

        val (gridSizeX, gridSizeY) = crossView.getGridSize()
        val (pixelSizeX, pixelSizeY) = crossView.getPenSize()

        val fragmentConfig = FragmentConfig.newInstance(gridSizeX, gridSizeY, pixelSizeX, pixelSizeY)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragmentConfig, "ConfigFragment")
            .addToBackStack("ConfigFragment")
            .commit()
    }

    private fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
    }

    private lateinit var viewPixelCounter: View
    private var popupPixelCounter: PopupWindow? = null
    private var isPopupPixelCounterShowed: Boolean = false

    @SuppressLint("InflateParams")
    private fun createPixelCounterPopup() {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewPixelCounter = inflater.inflate(R.layout.pixel_counter, null)

        popupPixelCounter =
                PopupWindow(viewPixelCounter, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val view = View(this)

        popupPixelCounter?.isOutsideTouchable = true

        popupPixelCounter?.isFocusable = true

        popupPixelCounter?.showAtLocation(view, Gravity.CENTER, 0, 0)

        popupPixelCounter?.dimBehind()

        isPopupPixelCounterShowed = true

        crossView.initPixelCounterRecyclerView(this, viewPixelCounter)
    }

    override fun onBackPressed() {
        when {
            (supportFragmentManager.findFragmentByTag("ConfigFragment") != null) -> {
                supportFragmentManager.popBackStack()
                cross_stitch_in_activity_main.visibility = View.VISIBLE

                container.visibility = View.GONE

                crossView.invalidate()
            }
            else -> super.onBackPressed()
        }
    }

    fun changeGridSize(x: Int, y: Int) {
        crossView.changeGridSize(x, y)
    }

    fun changePixelSize(x: Int, y: Int) {
        crossView.changePenSize(x, y)
    }

    fun initPixelLimitRecyclerView(view: View) {
        crossView.initPixelLimitRecyclerView(this, view)
    }
}
