package com.joaomadeira.android.appddm;

import android.app.Activity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistoActivity extends AppCompatActivity {

    Button mbtnRegistar;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    private EditText mEtNome;
    private EditText mEtApelido;
    private EditText mEtEmail;
    private EditText mEtTelefone;
    private TextView mTvMorada;
    private EditText mEtPass;
    private EditText mEtPassConf;
    private EditText mEtIdade;

    private TextView mTvPass;

    private final static String KEY_NOME = "KEY_NOME";
    private final static String KEY_USER = "KEY_USER";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    Cliente cliente;

    Button mBtnLocalizar;

    private static double mLongitude;
    private static double mLatitude;

    private static EditText mEtNomeG;
    private static EditText mEtApelidoG;
    private static EditText mEtEmailG;
    private static EditText mEtTelefoneG;
    private static EditText mEtPassG;
    private static EditText mEtPassConfG;
    private static EditText mEtIdadeG;

    static boolean primeiroRegisto = true;
    static boolean erroRgisto = false;
    static String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registar_ll);

        init();
    }//onCreate

    void init (){
        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null){
                    user.getProviderData();
                    Intent intent = new Intent(RegistoActivity.this, MainCliente.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mUtil = new AmUtil(RegistoActivity.this);
        mEtNome = (EditText) findViewById(R.id.idEtNome);
        mEtApelido = (EditText)findViewById(R.id.idEtApelido);
        mEtEmail = (EditText)findViewById(R.id.idEtEmail);
        mEtTelefone = (EditText)findViewById(R.id.idEtTelefone);
        mTvMorada = (TextView)findViewById(R.id.idTvMorada);
        mEtPass = (EditText)findViewById(R.id.idEtPass);
        mEtPassConf = (EditText)findViewById(R.id.idEtPassConf);
        mEtIdade = (EditText)findViewById(R.id.idEtIdade);
        mTvPass = (TextView)findViewById(R.id.idTvErro);
        mBtnLocalizar = (Button)findViewById(R.id.idBtnLocalizar);
        mbtnRegistar= (Button)findViewById(R.id.idBtnRegistar);

        if(!primeiroRegisto){
            mEtNome.setText(mEtNomeG.getText());
            mEtApelido.setText(mEtApelidoG.getText());
            mEtEmail.setText(mEtEmailG.getText());
            mEtTelefone.setText(mEtTelefoneG.getText());
            mEtPass.setText(mEtPassG.getText());
            mEtPassConf.setText(mEtPassConfG.getText());
            mEtIdade.setText(mEtIdadeG.getText());

            mTvMorada.setText(mLatitude + "," + mLongitude);
        }
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnRegistar) {
                    String strEtNome, strEtApelido, strEtEmail, strEtTelefone,
                            strEtPass, strEtPassConf, strEtIdade;

                    strEtNome = mUtil.verEditTextValido(mEtNome);
                    strEtApelido = mUtil.verEditTextValido(mEtApelido);
                    strEtEmail = mUtil.verEditTextValido(mEtEmail);
                    strEtTelefone = mUtil.verEditTextValido(mEtTelefone);
                    strEtPass = mUtil.verEditTextValido(mEtPass);
                    strEtPassConf = mUtil.verEditTextValido(mEtPassConf);
                    strEtIdade = mUtil.verEditTextValido(mEtIdade);

                    if ((strEtPass.equalsIgnoreCase(strEtPassConf)) && strEtPass != null) {
                        if (strEtNome != null && strEtApelido != null && strEtEmail != null && strEtTelefone != null
                                && strEtIdade != null) {

                            Localizacao morada = new Localizacao(mLatitude,mLongitude);

                            cliente = new Cliente(strEtNome, strEtApelido, strEtEmail, Integer.parseInt(strEtIdade), morada,
                                    Integer.parseInt(strEtTelefone));
                            pass = strEtPass;
                            mAuth.createUserWithEmailAndPassword(strEtEmail, strEtPass).addOnCompleteListener(RegistoActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        mUtil.utilFeedback("Erro de Registo");
                                        erroRgisto = true;
                                    } else {
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child("Cliente").child(user_id);
                                        current_user.setValue(cliente);
                                        erroRgisto = false;

                                        mAuth.signInWithEmailAndPassword(cliente.getEmail(), pass).addOnCompleteListener(RegistoActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    mUtil.utilFeedback("Erro de autenticação");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            mTvPass.setText("Campos errados! Verifique os dados introduzido.");
                        }
                    } else {
                        mTvPass.setText("Campos errados! Verifique os dados introduzido.");
                    }
                } else if(idQualOjectoInteragido==R.id.idBtnLocalizar){
                    Intent intent = MapLocalizacao.dados(RegistoActivity.this, mEtNome, mEtApelido, mEtEmail, mEtTelefone, mEtPass, mEtPassConf, mEtIdade, "Registo");
                    startActivity(intent);
                }
            }
        };
        mbtnRegistar.setOnClickListener(mClickHandler);
        mBtnLocalizar.setOnClickListener(mClickHandler);
    }//init

    public static Intent registarUser(
            Activity pChamador
    ){
        Intent ret = new Intent(
                pChamador,
                RegistoActivity.class
        );

        return ret;
    }//registarUser

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

    public static Intent backMorada(
            Activity pChamador,
            double pLatitude,
            double pLongitude,
            EditText pEtNome,
            EditText pEtApelido,
            EditText pEtEmail,
            EditText pEtTelefone,
            EditText pEtPass,
            EditText pEtPassConf,
            EditText pEtIdade
    ){
        Intent ret = new Intent(
                pChamador,
                RegistoActivity.class
        );
        mLatitude = pLatitude;
        mLongitude = pLongitude;
        mEtNomeG = pEtNome;
        mEtApelidoG = pEtApelido;
        mEtEmailG = pEtEmail;
        mEtTelefoneG = pEtTelefone;
        mEtPassG = pEtPass;
        mEtPassConfG = pEtPassConf;
        mEtIdadeG = pEtIdade;
        primeiroRegisto = false;
        return ret;
    }//backMorada
}
