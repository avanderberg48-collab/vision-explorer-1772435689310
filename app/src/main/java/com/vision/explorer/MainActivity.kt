package com.vision.explorer

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.util.* 

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var currentPathText: TextView
    private var currentDirectory: File = Environment.getExternalStorageDirectory()
    private var fileList: List<File> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) 
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.fileRecyclerView)
        currentPathText = findViewById(R.id.currentPathText)
        val backButton = findViewById<MaterialButton>(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        
        backButton.setOnClickListener {
            val parent = currentDirectory.parentFile
            if (parent != null && parent.canRead()) {
                loadDirectory(parent)
            } else {
                Toast.makeText(this, "At root directory", Toast.LENGTH_SHORT).show()
            }
        }

        loadDirectory(currentDirectory)
    }

    private fun loadDirectory(directory: File) {
        val files = directory.listFiles()
        if (files != null) {
            currentDirectory = directory
            currentPathText.text = "Current: ${directory.name}"
            currentPathText.contentDescription = "Currently viewing directory ${directory.name}"
            fileList = files.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase(Locale.ROOT) }))
            recyclerView.adapter = FileAdapter(fileList)
        } else {
            Toast.makeText(this, "Permission Denied or Folder Empty", Toast.LENGTH_LONG).show()
        }
    }

    inner class FileAdapter(private val files: List<File>) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val fileName: TextView = view.findViewById(R.id.fileName)
            val fileInfo: TextView = view.findViewById(R.id.fileInfo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_file, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val file = files[position]
            val type = if (file.isDirectory) "Folder" else "File"
            holder.fileName.text = file.name
            holder.fileInfo.text = "$type - Tap to open"
            
            // Accessibility focus and descriptions
            holder.itemView.contentDescription = "${file.name}, $type. Double tap to open."
            
            holder.itemView.setOnClickListener {
                if (file.isDirectory) {
                    loadDirectory(file)
                } else {
                    Toast.makeText(this@MainActivity, "Opening ${file.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount() = files.size
    }
}