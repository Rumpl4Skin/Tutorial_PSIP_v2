package com.example.tutorial_psip_v2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dd.processbutton.iml.ActionProcessButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class Registration extends Fragment {

    EditText firstName, lastName, mail, pass, codConf;
    ActionProcessButton send, reg;
    ProgressGenerator progressGenerator;
    ImageView img;
    Button qr_reader_open;
    Bitmap  _bitmapScaled ;
    String pin;
    NavController navController;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        firstName = view.findViewById(R.id.edtFirstName);
        lastName = view.findViewById(R.id.edtLastName);
        mail = view.findViewById(R.id.edtMail);
        pass = view.findViewById(R.id.edtPass);
        send = view.findViewById(R.id.btnCompiling);
        reg = view.findViewById(R.id.btnReg);
        codConf = view.findViewById(R.id.kod_conf);
        qr_reader_open = view.findViewById(R.id.qr_reader_open);

        progressGenerator = new ProgressGenerator();
        Intent intent = getActivity().getIntent();

        if(getArguments()!=null){//если вызывается после qr сканера
            firstName.setText(getArguments().getString("first_name"));
            lastName.setText(getArguments().getString("last_name"));
            mail.setText(getArguments().getString("mail"));
            pass.setText(getArguments().getString("pass"));
            codConf.setText(getArguments().getString("result"));
            pin=getArguments().getString("pin");

            firstName.setEnabled(false);
            lastName.setEnabled(false);
            send.setEnabled(false);
            mail.setEnabled(false);
            pass.setEnabled(false);
            codConf.setVisibility(View.VISIBLE);
            qr_reader_open.setVisibility(View.VISIBLE);
            reg.setVisibility(View.VISIBLE);
            send.setProgress(100);
        }


        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                try {
                 pin=random();
                    mDBHelper = new DatabaseHelper(getActivity());

                    try {
                        mDBHelper.updateDataBase();
                    } catch (IOException mIOException) {
                        throw new Error("UnableToUpdateDatabase");
                    }

                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }
                    Cursor cursor=mDb.rawQuery("select * from user where mail='"+mail.getText().toString()+"' ",null);
                    cursor.moveToFirst();
                 if(cursor.getCount()==0){//не одинаковые пользователи
                 if(allCorrect()&&isOnline()) {
                     firstName.setEnabled(false);
                     lastName.setEnabled(false);
                     send.setEnabled(false);
                     mail.setEnabled(false);
                     pass.setEnabled(false);
                     DownloadImageTask d = new DownloadImageTask(img);
                     d.execute();
                     SendMailImg im = new SendMailImg();
                     im.execute();
                     progressGenerator.start(send);
                     codConf.setVisibility(View.VISIBLE);
                     Toast.makeText(getContext(), "Если вы можете прочитать письмо на указанной почте с другого устройства, нажмите на кнопку чтения QR-кода или же вручную введите  полученный код",
                             Toast.LENGTH_LONG).show();
                     qr_reader_open.setVisibility(View.VISIBLE);
                     reg.setVisibility(View.VISIBLE);
                 }
                 else
                     Toast.makeText(getContext(), "Проверьте корректность введенных данных или соединение с интернетом!",
                             Toast.LENGTH_SHORT).show();}
                 else  Toast.makeText(getContext(), "Пользователь с такой почтой уже существует!",
                         Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                        Log.e("SendMail", e.getMessage(), e);
                        send.setProgress(-1);
                    send.setErrorText("Проверьте соединение с интернетом!");
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            send.setProgress(0);
                        }

                    }.start();
                    }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {//проверка соответствия кода и добавление в базу
            @Override
            public void onClick(View v) {
                if(codConf.getText().length()>=0&&codConf.getText().toString().substring(0,4).hashCode()==pin.substring(0,4).hashCode()){
                    mDBHelper = new DatabaseHelper(getActivity());

                    try {
                        mDBHelper.updateDataBase();
                    } catch (IOException mIOException) {
                        throw new Error("UnableToUpdateDatabase");
                    }

                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }
                    mDb.execSQL("INSERT into user(user_name,user_last_name,mail,pass) " +
                            "values('"+firstName.getText()+"','"+lastName.getText()+"','"+mail.getText()+"','"+pass.getText()+"')");
                    Toast.makeText(getContext(), "Вы успешно зарегистрировались! Попробуйте зайти под этой записью",
                            Toast.LENGTH_SHORT).show();
                    navController = Navigation.findNavController(getActivity(), R.id.fragment);
                    navController.navigate(R.id.autorization);
                }
                else {
                    Toast.makeText(getContext(), "Коды не совпадают, проверьте корректность введенного кода",
                            Toast.LENGTH_SHORT).show();
                    qr_reader_open.setEnabled(false);
                }
            }
        });

        qr_reader_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(getActivity(), R.id.fragment);
                //navController.navigate(R.id.QR_reader);

                Intent intent = new Intent(getActivity(), ScanCodActivity.class);
               intent.putExtra("first_name", firstName.getText().toString());
                intent.putExtra("last_name", lastName.getText().toString());
                intent.putExtra("mail", mail.getText().toString());
                intent.putExtra("pass", pass.getText().toString());
                intent.putExtra("pin", pin);
                startActivity(intent);
            }
        });
        return view;
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public String random(){
        String val = ""+((int)(Math.random()*9000)+1000);
        return  val;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask(ImageView bmImage) {
           img = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = "http://api.qrserver.com/v1/create-qr-code/?data="+pin+"&size=200x200";
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "rrrrrrrr");
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            _bitmapScaled=result;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            _bitmapScaled.compress(Bitmap.CompressFormat.PNG, 40, bytes);

//you can create a new file name "test.jpg" in sdcard folder.
            File f = new File(getContext().getFilesDir()+""
                    + File.separator + "test.png");
            try {
                f.createNewFile();

//write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

// remember close de FileOutput
                fo.close();}
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
        class SendMailImg extends AsyncTask<Void, Void, Boolean>{


            @Override
            protected Boolean doInBackground(Void... voids) {
                // Recipient's email ID needs to be mentioned.
                String to = mail.getText().toString();

                // Sender's email ID needs to be mentioned
                String from = "karchun.my@gmail.com";
                final String username = "karchun.my@gmail.com";//change accordingly
                final String password = "RTYzaq963";//change accordingly

                // Assuming you are sending email through relay.jangosmtp.net
                String host = "smtp.gmail.com";

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.port", "25");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {

                    // Create a default MimeMessage object.
                    Message message = new MimeMessage(session);

                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(from));

                    // Set To: header field of the header.
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(to));

                    // Set Subject: header field
                    message.setSubject("Подтверждение регистрации в 'Обучающее приложение по дисциплине ПСИП'");

                    // This mail has 2 part, the BODY and the embedded image
                    MimeMultipart multipart = new MimeMultipart("related");

                    // first part (the html)
                    BodyPart messageBodyPart = new MimeBodyPart();

                    Charset cset = Charset.forName("UTF-8");
                    String htmlText = "<meta http-equiv='Content-Type' charset='UTF-8' /><H1>Здравствуйте,"+firstName.getText()+" "+lastName.getText()+"!</H1><big>Если вы открыли это сообщение на мобильном устройстве -- введите этот код <big><b>"+pin+"</b></big><br></big>" +
                            "иначе нажмите на кнопку 'Открыть QR-READER' и наведите камеру на это изображение:<br><br><br><img src=\"cid:image\">";
                    ByteBuffer buf = cset.encode(htmlText);
                    byte[] b = buf.array();
                    String str = new String(b);

                    messageBodyPart.setContent(str, "text/html; charset=UTF-8");
                    // add it
                    multipart.addBodyPart(messageBodyPart);

                    // second part (the image)
                    messageBodyPart = new MimeBodyPart();
                    DataSource fds = new FileDataSource(
                            getContext().getFilesDir()+""
                                    + File.separator + "test.png");

                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<image>");

                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);

                    // put everything together
                    message.setContent(multipart);
                    // Send message
                    Transport.send(message);

                    System.out.println("Sent message successfully....");

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        }
        public boolean allCorrect(){
        if(firstName.getText().length()!=0&&lastName.getText().length()!=0&&mail.getText().length()!=0&&pass.getText().length()!=0)
        return true;
        else return false;
        }
        }

