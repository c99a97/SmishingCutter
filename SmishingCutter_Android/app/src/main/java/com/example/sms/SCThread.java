package com.example.sms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SCThread extends Thread {
    private final String topLevelDomain = "\\.bar|\\.bible|\\.biz|\\.church|\\.club|\\.college|\\.com|\\.design|\\.download|\\.green|\\.hiv|\\.info|\\.ist|\\.kaufen|\\.kiwi|\\.lat|\\.moe|\\.name|\\.net|\\.ninja|\\.one|\\.OOO|\\.org|\\.pro|\\.wiki|\\.xyz"
            +"|\\.aero|\\.asia|\\.cat|\\.eus|\\.coop|\\.edu|\\.gov|\\.int|\\.jobs|\\.mil|\\.mobi|\\.museum|\\.post|\\.tel|\\.tokyo|\\.travel|\\.xxx"
            +"|\\.alsace|\\.berlin|\\.brussels|\\.bzh|\\.cymru|\\.frl|\\.gal|\\.gent|\\.irish|\\.istanbul|\\.kiwi|\\.krd|\\.miami|\\.nyc|\\.paris|\\.quebec|\\.saarland|\\.scot|\\.vlaanderen|\\.wales|\\.wien"
            +"|\\.arpa|\\.example|\\.invalid|\\.local|\\.localhost|\\.onion|\\.test"
            +"|\\.ac|\\.ad|\\.ae|\\.af|\\.ag|\\.ai|\\.al|\\.am|\\.ao|\\.aq|\\.ar|\\.as|\\.at|\\.au|\\.aw|\\.ax|\\.az"
            +"|\\.ba|\\.bb|\\.bd|\\.be|\\.bf|\\.bg|\\.bh|\\.bi|\\.bj|\\.bm|\\.bn|\\.bo|\\.br|\\.bs|\\.bt|\\.bw|\\.by|\\.bz"
            +"|\\.ca|\\.cc|\\.cd|\\.cf|\\.cg|\\.ch|\\.ci|\\.ck|\\.cl|\\.cm|\\.cn|\\.co\\.kr|\\.co|\\.cr|\\.cu|\\.cv|\\.cw|\\.cx|\\.cy|\\.cz"
            +"|\\.de|\\.dj|\\.dk|\\.dm|\\.do|\\.dz"
            +"|\\.ec|\\.ee|\\.eg|\\.er|\\.es|\\.et|\\.eu"
            +"|\\.fi|\\.fj|\\.fk|\\.fm|\\.fo|\\.fr"
            +"|\\.ga|\\.gd|\\.ge|\\.gf|\\.gg|\\.gh|\\.gi|\\.gl|\\.gm|\\.gn|\\.gp|\\.gq|\\.gr|\\.gs|\\.gt|\\.gu|\\.gw|\\.gy"
            +"|\\.hk|\\.hm|\\.hn|\\.hr|\\.ht|\\.hu"
            +"|\\.id|\\.ie|\\.il|\\.im|\\.in|\\.io|\\.iq|\\.ir|\\.is|\\.it"
            +"|\\.je|\\.jm|\\.jo|\\.jp"
            +"|\\.ke|\\.kg|\\.kh|\\.ki|\\.km|\\.kn|\\.kp|\\.kr|\\.kw|\\.ky|\\.kz"
            +"|\\.la|\\.lb|\\.lc|\\.li|\\.lk|\\.lr|\\.ls|\\.lt|\\.lu|\\.lv|\\.ly"
            +"|\\.ma|\\.mc|\\.md|\\.me|\\.mg|\\.mh|\\.mk|\\.ml|\\.mm|\\.mn|\\.mo|\\.mp|\\.mq|\\.mr|\\.ms|\\.mt|\\.mu|\\.mv|\\.mw|\\.mx|\\.my|\\.mz"
            +"|\\.na|\\.nc|\\.ne|\\.nf|\\.ng|\\.ni|\\.nl|\\.no|\\.np|\\.nr|\\.nu|\\.nz"
            +"|\\.om"
            +"|\\.pa|\\.pe|\\.pf|\\.pg|\\.ph|\\.pk|\\.pl|\\.pm|\\.pn|\\.pr|\\.ps|\\.pt|\\.pw|\\.py"
            +"|\\.qa"
            +"|\\.re|\\.ro|\\.rs|\\.ru|\\.rw"
            +"|\\.sa|\\.sb|\\.sc|\\.sd|\\.se|\\.sg|\\.sh|\\.si|\\.sk|\\.sl|\\.sm|\\.sn|\\.so|\\.sr|\\.ss|\\.st|\\.su|\\.sv|\\.sx|\\.sy|\\.sz"
            +"|\\.tc|\\.td|\\.tf|\\.tg|\\.th|\\.tj|\\.tk|\\.tl|\\.tm|\\.tn|\\.to|\\.tr|\\.tt|\\.tv|\\.tw|\\.tz"
            +"|\\.ua|\\.ug|\\.uk|\\.us|\\.uy|\\.uz"
            +"|\\.va|\\.vc|\\.ve|\\.vg|\\.vi|\\.vn|\\.vu"
            +"|\\.wf|\\.ws"
            +"|\\.ye|\\.yt"
            +"|\\.za|\\.zm|\\.zw";
    //private String regex = "(?i)((https?)://)?\\S+(("+topLevelDomain+")[/\b]?)\\S*";
    private final String regex = "(?i)\\S+("+topLevelDomain+")(/\\S*|\\b)";
    private final Pattern p = Pattern.compile(regex);
    private String receiver, sender, contents;
    private Context context;


    public SCThread(String receiver, String sender, String contents, Context context){
        this.receiver = receiver;
        this.sender = sender;
        this.contents = contents;
        this.context = context;
    }

    public void run() {
        final int port = 32768;
        final String serverIP = "52.78.225.200";
        int i;
        Socket socket = null;
        PrintWriter pw = null;
        BufferedReader br = null;
        Matcher m = p.matcher(contents);
        Intent intent = new Intent(context, OverlayService.class);

        for(i=0; m.find(); i++) {
            if(i==0){
                Log.d(this.getClass().getName(), "URL found");
                try {
                    socket = new Socket(serverIP, port);
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    pw = new PrintWriter(socket.getOutputStream(), true);
                    pw.println(receiver);
                    pw.println(sender);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(this.getClass().getName(), "@@@@@@");
                    e.printStackTrace();
                }
            }

            try {
                Log.d(this.getClass().getName(), i+" = "+m.group());
                pw.println(m.group());
                String url = m.group();
                Log.d(this.getClass().getName(), "URL Length = "+url);
                Log.d(this.getClass().getName(), "URL Length = "+Integer.toString(url.length()));
                if(url.length()>40){
                    url = url.replaceFirst("http://","");
                    url = url.replaceFirst("https://","");
                    url = url.substring(0,32)+"...";
                }
                String blockValue = br.readLine().split("=")[1];

                intent.putExtra("url_"+i, url);
                intent.putExtra("url_block_"+i, blockValue);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        if(i!=0) {
            intent.putExtra("url_num", i);
            context.startService(intent);
        }

        try {
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
// 다른 출력방법
    new SCToast().postToastMessage(resultMsg, context);
    pw.println(m.group());
    if(br.readLine().equals("0")){
        new SCToast().postToastMessage("\""+m.group()+"\" 는 \"위험한\" URL입니다!", context);
    }
*/
