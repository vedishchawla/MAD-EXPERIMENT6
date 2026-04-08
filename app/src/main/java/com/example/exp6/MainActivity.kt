package com.example.exp6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

/**
 * Exp 6: Fetch and display data from a REST API.
 *
 * This activity sends an HTTP GET request to JSONPlaceholder API,
 * parses the JSON response, and displays the data in a RecyclerView.
 */
class MainActivity : AppCompatActivity() {

    // API endpoint URL
    private val API_URL = "https://jsonplaceholder.typicode.com/users"

    // UI elements
    private lateinit var btnFetch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var recyclerView: RecyclerView

    // Data list
    private val userList = mutableListOf<User>()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        btnFetch = findViewById(R.id.btnFetch)
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
        recyclerView = findViewById(R.id.recyclerView)

        // Setup RecyclerView
        adapter = UserAdapter(userList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set click listener on Fetch button
        btnFetch.setOnClickListener {
            fetchDataFromAPI()
        }
    }

    /**
     * Sends an HTTP GET request to the REST API,
     * parses the JSON response, and updates the RecyclerView.
     */
    private fun fetchDataFromAPI() {
        // Show loading state
        progressBar.visibility = View.VISIBLE
        btnFetch.isEnabled = false
        tvStatus.text = "Fetching data from API..."
        userList.clear()
        adapter.notifyDataSetChanged()

        // Create a Volley request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create a GET request for JSON array
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            API_URL,
            null,
            { response ->
                // Success: Parse JSON response
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)

                        // Extract required data fields
                        val user = User(
                            id = jsonObject.getInt("id"),
                            name = jsonObject.getString("name"),
                            email = jsonObject.getString("email"),
                            phone = jsonObject.getString("phone"),
                            company = jsonObject.getJSONObject("company").getString("name")
                        )
                        userList.add(user)
                    }

                    // Update UI with fetched data
                    adapter.notifyDataSetChanged()
                    tvStatus.text = "Fetched ${userList.size} users successfully!"
                    progressBar.visibility = View.GONE
                    btnFetch.isEnabled = true

                } catch (e: JSONException) {
                    tvStatus.text = "Error parsing JSON: ${e.message}"
                    progressBar.visibility = View.GONE
                    btnFetch.isEnabled = true
                }
            },
            { error ->
                // Error: Show error message
                tvStatus.text = "Error: ${error.message}"
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                btnFetch.isEnabled = true
            }
        )

        // Add request to the queue (this sends the HTTP GET request)
        requestQueue.add(jsonArrayRequest)
    }
}
