package com.joaomadeira.android.appddm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by research on 2016-11-10.
 */

public class AmUtil {
    Activity mActivity;

    public void utilPopularSpinnerComOpcoes(
       Spinner pSpn,
       String[] pOpcoes
    )
    {

        ArrayAdapter<String> ad = new ArrayAdapter<String>(
            mActivity,
            android.R.layout.simple_spinner_item,
            pOpcoes
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pSpn.setAdapter(ad);
    }//utilPopularSpinnerComOpcoes

    //--------------------------------------------------------------------------------------------------------------
    public AmUtil(
        Activity pA,
        int pAppRequestCode
    )
    {
        this.mActivity = pA;
    }//AmUtil

    public AmUtil(
            Activity pA
    )
    {
        this.mActivity = pA;
    }//AmUtil

    //--------------------------------------------------------------------------------------------------------------
    public void utilFeedback (
        final String msg
    ){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(
                    mActivity,
                    msg,
                    Toast.LENGTH_LONG
                );
                t.show();
            }//run
        };//r

        //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
        mActivity.runOnUiThread(r);
    }//utilFeedback

    //--------------------------------------------------------------------------------------------------------------
    //permissions em runtime
    public Boolean utilCheckPermission(
        String pStrPermission
    )
    {
        int iCheckPermissionResult = ContextCompat.checkSelfPermission(
                mActivity,
                pStrPermission
                //Manifest.permission.CALL_PHONE
        );

        boolean bHasPermission =
            (iCheckPermissionResult == PackageManager.PERMISSION_GRANTED);

        return bHasPermission;
    }//utilCheckPermission

    //--------------------------------------------------------------------------------------------------------------
    public boolean utilModernRequestPermission(
            String strPermissionToAsk,
            int pAppRequestCode,
            Boolean bShowToast //alert opcional
    ){
        boolean bHasPermission =
                utilCheckPermission(strPermissionToAsk);

        if (!bHasPermission)
        {
            try {
                ActivityCompat.requestPermissions(
                        mActivity, //Activity
                        //new String[]{Manifest.permission.CALL_PHONE}, //array of permissions
                        new String[]{strPermissionToAsk}, //array of permissions
                        //THIS_APP_REQUEST_CODE //request code
                        pAppRequestCode
                );
                if (bShowToast) {
                    utilFeedback(strPermissionToAsk + " GRANTED!");
                }
                return true;
            }//try
            catch (Exception e){
                if (bShowToast) {
                    utilFeedback(strPermissionToAsk + " NOT GRANTED!");
                }
                return false;
            }//catch
        }//if
        return false;
    }//utilModernRequestPermission

    //--------------------------------------------------------------------------------------------------------------
    public boolean utilModernRequestPermissions(
            String[] strPermissionsToAsk,
            int pAppRequestCode,
            Boolean bShowToast //alert opcional
    ){
        ArrayList<String> alPermissionsThatMustBeRequested =
                new ArrayList<String>();

        for (String strPermission : strPermissionsToAsk){
            boolean bAlreadyAllowed = utilCheckPermission(
                strPermission
            );
            if(!bAlreadyAllowed) {
                alPermissionsThatMustBeRequested.add(strPermission);
            }
        }//for

        int iHowManyPermissionsToRequest = alPermissionsThatMustBeRequested.size();

        String[] straPermissionsThatMustReallyBeRquested =
            new String[iHowManyPermissionsToRequest];

        String strAllPermissions = "";
        for (int idx=0; idx<iHowManyPermissionsToRequest; idx++){
            straPermissionsThatMustReallyBeRquested[idx] =
                alPermissionsThatMustBeRequested.get(idx);
            strAllPermissions += alPermissionsThatMustBeRequested.get(idx) + "\n";
        }//for

        try {
            ActivityCompat.requestPermissions(
                    mActivity, //Activity
                    //new String[]{Manifest.permission.CALL_PHONE}, //array of permissions
                    straPermissionsThatMustReallyBeRquested, //array of permissions
                    //THIS_APP_REQUEST_CODE //request code
                    pAppRequestCode
            );
            if (bShowToast) {
                utilFeedback(strAllPermissions + " REQUESTED!");
            }
            return true;
        }//try
        catch (Exception e) {
            if (bShowToast) {
                utilFeedback(strAllPermissions + " NOT REQUESTED!");
            }
            return false;
        }//catch
    }//utilModernRequestPermissions

    //--------------------------------------------------------------------------------------------------------------
    /*
    permission.Internet => false
    permission.Xpto => true
     */
    public HashMap<String, Boolean> utilModernRequestPermissions1(
        String[] straPermissionsToAsk,
        int pAppRequestCode,
        Boolean bShowToast
    ) {
        HashMap<String, Boolean> ret = new HashMap<>();

        for(String strPermission : straPermissionsToAsk){
            Boolean requestResult =
                utilModernRequestPermission(
                    strPermission,
                    pAppRequestCode,
                    bShowToast
                );
            ret.put(strPermission, requestResult);
        }//for
        return ret;
    }//utilModernRequestPermissions1

    //--------------------------------------------------------------------------------------------------------------
    /*
    indiretamente util para determinar a presença de certos
    componentes nos dispositivos
    Por exemplo, se um Intent de CALL_PHONE não for suportado
    é provavelmente pq o dispositivo não tem tefefonia nóvel
     */
    public Boolean utilCheckSupportedIntent(
        Intent pIntent
    ){
        PackageManager pm = mActivity.getPackageManager();
        Boolean bThereIsAtLeastOneAppCapableOfRespondingToTheIntent =
                pIntent.resolveActivity(pm)!=null;

        return bThereIsAtLeastOneAppCapableOfRespondingToTheIntent;
    }//utilCheckSupportedIntent

    //--------------------------------------------------------------------------------------------------------------
    public String readIntFromEditText
    (
            EditText pEditText
    ) throws Exception {
        String strQueEstaEscrito = pEditText.getText().toString();
        strQueEstaEscrito = strQueEstaEscrito.trim();
        if(strQueEstaEscrito!=""){
            //????????TODO
        }
        //int iRet;
        String iRet;
        try {
            iRet = /*Integer.parseInt(*/strQueEstaEscrito;//);
            return iRet;
        }//try
        catch (Exception e){
            //TODO: suportar esta Exception
            String strMsg =
                    String.format("@readIntEditText, can not convert string %s",
                            strQueEstaEscrito
                    );
            throw new Exception(strMsg);
            //return 0;
        }//catch
    }//readIntFromEditText

    public String verEditTextValido(
        EditText pEditText
    ){
        String strQueEstaEscrito = pEditText.getText().toString();
        strQueEstaEscrito = strQueEstaEscrito.trim();
        if(strQueEstaEscrito==""){
            //????????TODO
            strQueEstaEscrito = null;
        }
        return strQueEstaEscrito;
    }
}//AmUtil
