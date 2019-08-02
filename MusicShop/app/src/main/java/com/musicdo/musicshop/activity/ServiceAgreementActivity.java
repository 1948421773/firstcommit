package com.musicdo.musicshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicdo.musicshop.MyApplication;
import com.musicdo.musicshop.R;
import com.musicdo.musicshop.constant.AppConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/*import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;*/
/*import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;*/
import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;

/**
 * 服务协议
 * Created by Yuedu on 2017/9/19.
 */

public class ServiceAgreementActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_title,tv_service_agreement;
    private ImageView iv_back;
    private WebView w_serviceagreement;
    private String docName = "serviceagreement.txt";
    private String savePath = AppConstants.SAVEFILE;
    StringBuffer sb;
    public static final String ENCODING = "GBK";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceagreement);
        MyApplication.getInstance().addActivity(this);
        context=this;
            initView();
    }

    private void initView() {
        w_serviceagreement=(WebView)findViewById(R.id.w_serviceagreement);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_service_agreement=(TextView)findViewById(R.id.tv_service_agreement);
        tv_title.setText("服务协议");
        iv_back.setOnClickListener(this);
        tv_service_agreement.setText(getFromAssets(docName));

        /*String name = docName.substring(0, docName.indexOf("."));
        try {
            if(!(new File(savePath+name).exists()))
                new File(savePath+name).mkdirs();
//            convert2Html(docName,savePath+name+".html");
        } catch (Exception e){
            e.printStackTrace();
        }
        //WebView加载显示本地html文件
        WebView webView = (WebView)this.findViewById(R.id.w_serviceagreement);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl("file:/"+savePath+name+".html");*/
    }

    public String getFromAssets(String fileName) {
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
                // 获取文件的字节数
            int lenght = in.available();
                // 创建byte数组
            byte[] buffer = new byte[lenght];
                // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * word文档转成html格式
     * */
    /*public void convert2Html(String fileName, String outPutFile)
            throws TransformerException, IOException,
            ParserConfigurationException {
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(String.valueOf(context.getAssets().open(fileName))));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

        //设置图片路径
        wordToHtmlConverter.setPicturesManager(new PicturesManager()
        {
            public String savePicture(byte[] content,
                                      PictureType pictureType, String suggestedName,
                                      float widthInches, float heightInches )
            {
                String name = docName.substring(0,docName.indexOf("."));
                return name+"/"+suggestedName;
            }
        } );

    //保存图片
    List<Picture> pics=wordDocument.getPicturesTable().getAllPictures();
        if(pics!=null){
        for(int i=0;i<pics.size();i++){
            Picture pic = (Picture)pics.get(i);
            System.out.println( pic.suggestFullFileName());
            try {
                String name = docName.substring(0,docName.indexOf("."));
                pic.writeImageContent(new FileOutputStream(savePath+ name + "/"
                        + pic.suggestFullFileName()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
        wordToHtmlConverter.processDocument(wordDocument);
    Document htmlDocument = wordToHtmlConverter.getDocument();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DOMSource domSource = new DOMSource(htmlDocument);
    StreamResult streamResult = new StreamResult(out);

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
    //保存html文件
    writeFile(new String(out.toByteArray()), outPutFile);
}*/

    /**
     * 将html文件保存到sd卡
     * */
    public void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
