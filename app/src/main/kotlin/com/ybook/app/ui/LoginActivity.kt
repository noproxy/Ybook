package com.ybook.app.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ybook.app.R
import com.ybook.app.swipebacklayout.SwipeBackActivity

import java.util.ArrayList
import com.ybook.app.id
import android.app.ProgressDialog
import me.toxz.kotlin.after
import com.koushikdutta.ion.Ion
import com.koushikdutta.async.http.WebSocket.StringCallback
import com.koushikdutta.async.http.AsyncHttpClient
import com.ybook.app.util.JSONHelper
import android.util.Log
import com.ybook.app.util.AccountHelper
import com.ybook.app.net.LoginRequest
import com.ybook.app.bean.getLibCode
import me.toxz.kotlin.makeTag
import android.os.Handler
import com.koushikdutta.async.http.WebSocket


/**
 * A login screen that offers login via account/password.
 */
public class LoginActivity : SwipeBackActivity() {

    private val TAG = makeTag()

    // UI references.
    private var mEmailView: AutoCompleteTextView? = null
    private var mPasswordView: EditText? = null
    private var mLoginFormView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupActionBar()
        initViews()
        // Set up the login form.

    }

    private fun initViews() {
        mEmailView = id(R.id.email) as AutoCompleteTextView
        mPasswordView = (id(R.id.password) as EditText).after {
            it setOnEditorActionListener {(textView: TextView, id: Int, keyEvent: KeyEvent) ->
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin()
                    true
                } else false
            }
        }
        (id(R.id.sign_in_button) as Button).let { it setOnClickListener { attemptLogin() } }
        mLoginFormView = id(R.id.login_form)
    }


    /**        dialog.show();

     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public fun attemptLogin() {

        // Reset errors.
        mEmailView!!.setError(null)
        mPasswordView!!.setError(null)

        // Store values at the time of the login attempt.
        val account = mEmailView!!.getText().toString()
        val password = mPasswordView!!.getText().toString()

        var cancel = false
        var focusView: View? = null


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView!!.setError(getString(R.string.error_invalid_password))
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid account address.
        if (TextUtils.isEmpty(account)) {
            mEmailView!!.setError(getString(R.string.error_field_required))
            focusView = mEmailView
            cancel = true
        } else if (!isAccountValid(account)) {
            mEmailView!!.setError(getString(R.string.error_invalid_email))
            focusView = mEmailView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            login({ onFail() }, { onSucceed(it) }, LoginRequest(account, password, getLibCode(), null))
        }
    }

    private fun isAccountValid(account: String): Boolean {
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length() > 4
    }

    var mProgressDialog: ProgressDialog? = null

    /**
     * Shows the progress UI and hides the login form.
     */
    TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public fun showProgress(show: Boolean) {
        if (mProgressDialog == null) mProgressDialog = ProgressDialog(this@LoginActivity).after {
            it setMessage "Please wait while loading..."
            it setIndeterminate true
            it setCancelable false
        }
        if (show) mProgressDialog!!.show() else mProgressDialog!!.dismiss()
    }

    private fun onFail() {
        showProgress(false)
        mPasswordView!!.setError(getString(R.string.error_incorrect_password))
        mPasswordView!!.requestFocus()
    }

    private fun onSucceed(request: LoginRequest) {
        showProgress(false)
        AccountHelper.storeAccount(request, this@LoginActivity)
        Log.i(TAG, "login success")
        finish()
    }

    var mHandler = Handler()

    fun login(failed: () -> Unit, succeed: (request: LoginRequest) -> Unit, request: LoginRequest) {

        Log.i(TAG, "Login start")
        val mainUrl = "//TODO"
        val url = mainUrl + "/profile"
        Log.i(TAG, "url: " + url)
        var isEnd = false
        AsyncHttpClient.getDefaultInstance().websocket(url, "my-protocol", {
            (exception, webSocket) ->
            Log.i(TAG, "socket get:" + webSocket)
            if (webSocket == null) {
                this@LoginActivity.runOnUiThread(failed)
            } else {
                webSocket.setClosedCallback {
                    Log.i(TAG, "closed")
                    if (!isEnd)
                        this@LoginActivity.runOnUiThread (failed)
                }

                webSocket.setStringCallback {
                    val rep = JSONHelper.readLoginResponse(it)
                    Log.i(TAG, "login status:" + rep.status)
                    if (rep.status == 0)
                        this@LoginActivity.runOnUiThread { succeed(request) }
                    else
                        this@LoginActivity.runOnUiThread(failed)
                    isEnd = true
                }
                webSocket.send(request.getJSONStr())
            }

        })
        Log.i(TAG, "Task over")
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    //    public inner class UserLoginTask(private val mAccount: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {
    //        val mRequest = LoginRequest(mAccount, mPassword, getLibCode(), null)
    //
    //        override fun doInBackground(vararg params: Void): Boolean? {
    //            Log.i(TAG, "Task start")
    //            val mainUrl = getMainUrl()
    //            val url = mainUrl + "/profile"
    //            Log.i(TAG, "url: " + url)
    //            var success = false
    //            AsyncHttpClient.getDefaultInstance().websocket(url, "my-protocol", {
    //                (exception, webSocket) ->
    //                Log.i(TAG, "socket get")
    //                webSocket.setClosedCallback { Log.i(TAG, "closed") }
    //                webSocket.setStringCallback {
    //                    val rep = JSONHelper.readLoginResponse(it)
    //                    Log.i(TAG, "login status:" + rep.status)
    //                    success = rep.status == 0
    //                }
    //                webSocket.send(mRequest.getJSONStr())
    //            })
    //            Log.i(TAG, "Task over")
    //            return success
    //        }
    //
    //        override fun onPostExecute(success: Boolean) {
    //            mAuthTask = null
    //            showProgress(false)
    //
    //            if (success) {
    //                AccountHelper.storeAccount(mRequest, this@LoginActivity)
    //                Log.i(TAG, "login success")
    //                finish()
    //            } else {
    //                mPasswordView!!.setError(getString(R.string.error_incorrect_password))
    //                mPasswordView!!.requestFocus()
    //            }
    //        }
    //
    //        override fun onCancelled() {
    //            mAuthTask = null
    //            showProgress(false)
    //        }
    //    }

}



