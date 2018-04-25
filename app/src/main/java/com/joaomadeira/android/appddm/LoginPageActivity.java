package com.joaomadeira.android.appddm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPageActivity extends AppCompatActivity {

    Button mbtnLogin;
    TextView mTvRegistar;
    View.OnClickListener mClickHandler;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private AmUtil mUtil;

    private EditText mEtUser;
    private EditText mEtPasse;

    private TextView mTvErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ll);

        init();
    }//onCreate

    void init (){
        mUtil = new AmUtil(LoginPageActivity.this);

        mEtUser = (EditText) findViewById(R.id.idEtUser);
        mEtPasse = (EditText)findViewById(R.id.idEtPasse);

        mTvErro = (TextView) findViewById(R.id.idTvErro);

        mbtnLogin= (Button)findViewById(R.id.idBtnLogin);
        mTvRegistar=(TextView)findViewById(R.id.idTvRegistar);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null) {
                    final String idUser = user.getUid();
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference users = root.child("Utilizadores");
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.child("Cliente").child(idUser).exists()) {
                                Intent intent = new Intent(LoginPageActivity.this, MainCliente.class);
                                startActivity(intent);
                                finish();
                                return;
                            } else if (snapshot.child("Condutor").child(idUser).exists()) {
                                Intent intent = new Intent(LoginPageActivity.this, MainCondutor.class);
                                startActivity(intent);
                                finish();
                                return;
                            } else if (snapshot.child("Lavandaria").child(idUser).exists()) {
                                Intent intent = new Intent(LoginPageActivity.this, MainLavandaria.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnLogin){
                    String strEtUser, strEtPasse;
                    try {
                        strEtUser = mUtil.verEditTextValido(mEtUser);
                        strEtPasse = mUtil.verEditTextValido(mEtPasse);

                        if(strEtUser != null && strEtPasse != null){
                            mAuth.signInWithEmailAndPassword(strEtUser,strEtPasse).addOnCompleteListener(LoginPageActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        mTvErro.setText("Nome de utilizador ou password invalidos.");
                                    }
                                }
                            });
                        }
                    } catch (Exception e){
                        mUtil.utilFeedback("Por favor intoduza os seus dados");
                    }
                } else if(idQualOjectoInteragido==R.id.idTvRegistar){
                    try {
                        Intent chamadorDeJogar = RegistoActivity.registarUser(
                                LoginPageActivity.this
                        );
                        startActivity(chamadorDeJogar);
                    } catch (Exception e){
                        mUtil.utilFeedback(e.toString());
                    }
                }
            }
        };//mClickHandler
        mbtnLogin.setOnClickListener(mClickHandler);
        mTvRegistar.setOnClickListener(mClickHandler);

    }//init

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }//onStop

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);

    }//onStart
}
