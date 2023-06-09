package com.example.final_pjt.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.final_pjt.R
import com.example.final_pjt.databinding.ActivityLoginBinding
import com.example.final_pjt.dto.User
import com.example.final_pjt.service.UserService
import com.example.final_pjt.util.ApplicationClass
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginActivity_싸피"
class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var userData:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        init()
        //기존 로그인 이력이 있었던 사람은 바로 Main으로 이동

        Glide.with(this).load(R.raw.thinking).override(500, 500).into(binding.loginChIv)
    }

    /**
     * 구글 로그인 및 리스너 초기화
     */
    private fun init(){
        createGoogleInstance()
        initListener()
    }
    private fun initListener(){
        binding.btnGoogleLogin.setOnClickListener {
            googleLogin()
        }
    }
    /**
     * 구글 로그인 옵션 설정
     */
    private fun createGoogleInstance(){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        var gsa = GoogleSignIn.getLastSignedInAccount(this)
        //기존 로그인 이력이 있는경우 바로 로그인
        if(gsa!=null){
            var intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("flag",1)
            startActivity(intent)
        }

        binding.btnGoogleLogin.setOnClickListener {
            googleLogin()
        }
    }
    private fun changePage(){
        var intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("flag",2)
        intent.putExtra("data",userData)
        startActivity(intent)
    }

    /**
     * 구글 로그인 런처 실행
     */
    fun googleLogin(){
        var signinIntent = googleSignInClient!!.signInIntent
        loginLauncher.launch(signinIntent)
    }
    /**
     * 구글 로그인 콜백 런처
     */
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.e(TAG,it.resultCode.toString())
        if(it.resultCode == Activity.RESULT_OK){
            Log.d(TAG, ": success")
            val task = Auth.GoogleSignInApi.getSignInResultFromIntent(it.data!!)
            val taskData : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data!!)
            if(task!!.isSuccess){
                var account = task.signInAccount
                firebaseAuthWithGoogle(account)
                handleSignInReuslt(taskData)
                changePage()
            }
            else{
                Log.d(TAG, ":fail ")
            }
        }
    }

    /**
     * Google로 부터 로그인한 사용자 데이터 전송 받음
     */
    private fun handleSignInReuslt(completedTask:Task<GoogleSignInAccount>){
        try{
            val account = completedTask.getResult(ApiException::class.java)
            val token = auth?.uid.toString()
            val photoUrl = account?.photoUrl.toString()
            val nickName = account?.displayName.toString()
            var user = User(token,photoUrl,nickName)
            userData = user
            Log.d(TAG, "handleSignInReuslt: ${userData.toString()}")

        }catch (e : ApiException){
            Log.d(TAG, "handleSignInReuslt: ${e.message}")
        }
    }
    /**
     * Google 로그인 성공시 화면 이동 , 실패시 실패 메세지 토스트 출력
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)?.
        addOnCompleteListener {
                it->
            if(it.isSuccessful){
                Log.d(TAG, "firebaseAuthWithGoogle: ")
                changePage()
            }
            else {
                //Show the error message
                Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

   

    /**
     * 구글 계정 로그아웃
     */
    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

}