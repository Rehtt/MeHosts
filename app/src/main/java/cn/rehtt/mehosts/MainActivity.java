package cn.rehtt.mehosts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
Button button;
    TextView textView,textView4,textView5;
    Handler handler;

    String result="";

int p=1,a=0;
    ProgressBar p1;
    String urll="";

    int up=0;
    String content="";
    String jsonData ;
    int type = 0;
    String Durl ="";
    String BBen="";

    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.button);
        p1=(ProgressBar)findViewById(R.id.progressBar);
       textView=(TextView)findViewById(R.id.textView);
        textView4=(TextView)findViewById(R.id.textView4);
        textView5=(TextView)findViewById(R.id.textView5);
        updata();
        upgradeRootPermission(getPackageCodePath());

        if(RootTools.isAccessGiven()==false)  //检测是否已获取root
            dialog();



        button.setOnClickListener(new View.OnClickListener() {
            int o=0;
            @Override
            public void onClick(View v) {
                textView.setText("安装中");
                if(p1.getVisibility()==View.GONE)
                    p1.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                @Override
                public void run() {
                    send();

                    Message message=handler.obtainMessage();
                    handler.sendMessage(message);
                    RootTools.copyFile("/data/data/cn.rehtt.mehosts/files/hosts","/system/etc/hosts",true,true);
                    M();

                    o=1;
                }
            }).start();
                if(o==1){
                    textView.setText("安装完成");
                    p1.setVisibility(View.GONE);
                }





//                copyFile("/data/data/cn.rehtt.mehosts/files/hosts.txt","/system/etc/hosts.txt");

            }
        });



        handler=new Handler(){
            public void handleMessage(Message message){

                if(result!=""+"\n"){
                    save();


                }
                else {
                    Toast.makeText(getApplicationContext(),"检测网络",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(message);
            }
        };
    }

    public void send(){         //读取数据

        if(i==0) {
            urll = "https://raw.githubusercontent.com/racaljk/hosts/master/hosts";
        }
        else if(i==1) urll="https://raw.githubusercontent.com/sy618/hosts/master/y";
        else urll="https://raw.githubusercontent.com/sy618/hosts/master/p";
        URL url;
        try {
            url=new URL(urll);
            HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String string=null;
            while ((string=bufferedReader.readLine())!=null){

                result+=string+"\n";

            }
            inputStreamReader.close();
            if (i<2){
                i++;
            send();}
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save(){
        FileOutputStream outputStream=null;
        BufferedWriter bufferedWriter=null;
        try {
            outputStream=openFileOutput("hosts", Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(result);
            Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void M(){

        String[] command={"chmod","644","/system/ect/hosts"};
        ProcessBuilder processBuilder=new ProcessBuilder(command);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {

            return false;
        } finally {
            try {
                if (os != null) {

                    os.close();


                }




                process.destroy();
            } catch (Exception e) {

            }
        }
        return true;



    }

    protected void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("无root权限！！！");
        builder.setTitle("重要提示");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.create().show();

    }




//    public void copyFile(String oldPath, String newPath) {
//        try {
//            int bytesum = 0;
//            int byteread = 0;
//            File oldfile = new File(oldPath);
//            if (oldfile.exists()) { //文件存在时
//                InputStream inStream = new FileInputStream(oldPath); //读入原文件
//                FileOutputStream fs = new FileOutputStream(newPath);
//                byte[] buffer = new byte[1444];
//                int length;
//                while ( (byteread = inStream.read(buffer)) != -1) {
//                    bytesum += byteread; //字节数 文件大小
//                    System.out.println(bytesum);
//                    fs.write(buffer, 0, byteread);
//                }
//                inStream.close();
//            }
//        }
//        catch (Exception e) {
//
//            System.out.println("复制单个文件操作出错");
//            e.printStackTrace();
//
//        }
//        Toast.makeText(getApplicationContext(),"ko",Toast.LENGTH_LONG).show();
//
//    }




    public void updata(){

        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo info= packageManager.getPackageInfo(this.getPackageName(),0);
            String version=info.versionName;
            up=info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        urll="http://rehtt.cn/app/MeHosts/updata.php";
        new Location().execute();


    }

    class Location extends AsyncTask {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {


            try {
                URL httpUrl = new URL(urll);
                HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                OutputStream out = httpURLConnection.getOutputStream();
                out.write(content.getBytes());
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader br = new BufferedReader(in);
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                jsonData = sb.toString();

                in.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                type = jsonObject.getInt("type");

                Durl = jsonObject.getString("url");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            final Uri DDurl=Uri.parse(Durl);
            if (up != type) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("有新的版本！！！")
                .setTitle("重要提示")
                        .setNegativeButton("暂不更新", null);
                 builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent download=new Intent(Intent.ACTION_VIEW,DDurl);
                        startActivity(download);
                    }
                });
                builder.create().show();


            }
//            Toast.makeText(getApplicationContext(),"type:"+type+"\n"+"url:"+DDurl+"\n"+"up:"+up,Toast.LENGTH_LONG).show();
        }
    }

    private void aa() throws IOException {
        char[] a;
//        int i=0;
//        FileInputStream fileInputStream=new FileInputStream("/system/ect/hosts");
//        InputStreamReader inputStreamReader = null;
//        inputStreamReader = new InputStreamReader(fileInputStream);
//        BufferedReader reader = new BufferedReader(inputStreamReader);
//        StringBuffer sb = new StringBuffer("");
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//                sb.append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FileInputStream inputStream = this.openFileInput("/system/ect/hosts");
        byte[] bytes = new byte[100];
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        while (inputStream.read(bytes) != -1) {
            arrayOutputStream.write(bytes, 0, bytes.length);
        }
        inputStream.close();
        arrayOutputStream.close();

        String content = new String(arrayOutputStream.toByteArray());








    }


}
