package com.nice.nice.test.controller;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import zipUtil.CompressZip;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class testcontoller {
    private String cookie;
    public List<String> data = new ArrayList<>();
    public List<String> passFiles = new ArrayList<>();
    public List<String> failFiles = new ArrayList<>();
    private boolean commit = true;
    public String status = "";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        data.add("");
        System.out.println("main in!");
        return "main";
    }

    @RequestMapping(value = "/main.do", method = RequestMethod.GET)
    public String indexMain(Model model) {
        data.add("");
        System.out.println("main in!");
        return "main";
    }

    @RequestMapping(value = "/commit.do", method = RequestMethod.POST)
    public ModelAndView ModelAndView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("main");
        passFiles.clear();
        failFiles.clear();
        data.clear();
        status="";

        String chk = request.getParameter("chk");

        String name = "admin";
        String password = "1234qwer!";
        String project = "zip";
        String filePath ="";
        if(chk.equals("a"))
            filePath = "C:\\Users\\CODEMIND-Romy\\Desktop\\nie\\testsource\\";
        else
            filePath = "C:\\Users\\CODEMIND-Romy\\Desktop\\nie\\testsource2\\";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username",name);
        params.add("password",password);
        params.add("REQUEST_KIND","API");

        HttpEntity entity = new HttpEntity(params,null);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/user/login/process", HttpMethod.POST, entity, String.class);
            cookie = responseEntity.getHeaders().get("SET-COOKIE").get(0);
            //data.add(responseEntity.getStatusCode() + "\n" + responseEntity.getBody()+"\n\n");
            status = "Login Success";

            response.addHeader("SET-COOKIE", cookie);
            System.out.println(responseEntity.getStatusCode());
            System.out.println(responseEntity.getBody());
            if (anal(project, filePath)) {
                resultChk(project);
            } else{
            }
            if (commit) {
                status = "";
                commit(project);
            } else{
                status="";
                data.add("COMMIT FAIL");
                if(failFiles.size() != 0)
                    data.add(failFiles.size()+"개의 파일에서 10개 이상의 경고가 검출되었습니다.");
            }
            mav.addObject("data", data);
            mav.addObject("pass",passFiles);
            mav.addObject("fail",failFiles);
        } catch (Exception e){
            data.add("Fail\n\n");
            mav.addObject("data",data);
        }
        return mav;
    }

    public boolean anal(String project, String filepath){

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie",cookie);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            commit = true;
            List<String> files = new ArrayList<>();

            //findFile(filepath,files);

            CompressZip zip = new CompressZip();
            try {
                if (!zip.compress(filepath, filepath, project))
                { System.out.println("압축 실패"); }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            File testfile = new File(filepath+project+".zip");
//
           MultipartFile multipartFile = new MockMultipartFile(project, new FileInputStream(testfile));
//

//
            ByteArrayResource fileResource = new ByteArrayResource((multipartFile.getBytes())){
                // 기존 ByteArrayResource의 getFilename 메서드 override
                @Override
                public String getFilename() {
                    return project+".zip";
                }
            };

//            String analFiles = "";
//
//            for(int i=0;i<files.size();i++){
//                if(i!=files.size()-1)
//                    analFiles += files.get(i)+",";
//                else
//                    analFiles += files.get(i);
//            }

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("projectname",project);
//            params.add("filelist",analFiles);
            params.add("file",fileResource);
            HttpEntity entity = new HttpEntity(params,headers);

            RestTemplate restTemplate = new RestTemplate();
           // ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/api/analyzeCluster", HttpMethod.POST, entity, String.class);
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/api/upload", HttpMethod.POST, entity, String.class);
            //data.add(responseEntity.getStatusCode() + "\n" + responseEntity.getBody()+"\n\n");
            status="Anal Start";
            Thread.sleep(2000);
            if(analCheck(project).contains("success")) {
                //data.add("anal complete!");
                System.out.println(responseEntity.getStatusCode());
                System.out.println(responseEntity.getBody());
                status = "Anal Complete";
                return true;
            }
            else {
                //data.add("anal fail!");
                status = "Anal Fail";
                System.out.println(responseEntity.getStatusCode());
                System.out.println(responseEntity.getBody());
                return false;
            }

        }catch (Exception e){
            status = "Anal Fail";
            //data.add("anal fail\n\n");
            return false;
        }
    }

    public void resultChk(String project){
        try{
            status="Result checking";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie",cookie);

            HttpEntity entity = new HttpEntity(null,headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/api/projects/" + project + "/fileresult", HttpMethod.GET, entity, String.class);
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(responseEntity.getBody());

            for(int i=0;i<array.size();i++){
                JSONObject obj = (JSONObject) array.get(i);
                JSONObject risky = (JSONObject) obj.get("risky");
                long total = 0;
                for(int j=1;j<6;j++){
                    if(risky.get(Integer.toString(j))!=null)
                        total += (long)risky.get(Integer.toString(j));
                }
                if(total >= 10) {
                    commit = false;
                    failFiles.add(obj.get("fileName")+" : "+total+"개의 경고 검출");
                } else{
                    passFiles.add(obj.get("fileName")+" : "+total+"개의 경고 검출");
                }
            }
            System.out.println(responseEntity.getStatusCode()+", "+responseEntity.getBody());

            saveResult(responseEntity.getBody());
        }catch (Exception e){
            status = "Get Result Fail";
            //data.add("get result fail");
        }
    }

    public String analCheck(String projectname){
        String status = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);

            HttpEntity entity = new HttpEntity(null, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/api/" + projectname + "/status", HttpMethod.GET, entity, String.class);
            status = responseEntity.getBody().split("status\":\"")[1];
            if (status.contains("progressing") || status.contains("unziping") || status.contains("reserved") || status.contains("onthefly")) {
                Thread.sleep(5000);
                status = analCheck(projectname);
            }
        }catch (Exception e){

        }

        return status;
    }

//    public static void findFile(String path,List<String> filelist){
//        File[] files = new File(path).listFiles();
//
//        for(File f : files){
//            if(f.isDirectory()) {
//                findFile(f.getAbsolutePath(),filelist);
//            } else {
//                filelist.add(f.getAbsolutePath());
//            }
//        }
//    }

    @RequestMapping(value = "/data.do")
    @ResponseBody
    public String data(){
        return status;
    }

    public void commit(String project){
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("project", project);
            params.add("status", "success");

            HttpEntity entity = new HttpEntity(params, null);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity responseEntity = restTemplate.exchange("http://localhost:8088/commit", HttpMethod.POST, entity, String.class);
            data.add("COMMIT SUCCESS");
            System.out.println(responseEntity.getStatusCode()+", "+responseEntity.getBody());
        }catch (Exception e){
            data.add("COMMIT FAIL");
        }
    }


    private void saveResult(String result) {
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("result", result);

            HttpEntity entity = new HttpEntity(params, null);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8088/saveDB", HttpMethod.POST, entity, String.class);
            data.add("SAVE SUCCESS");
            System.out.println(responseEntity.getStatusCode() + ", " + responseEntity.getBody());
        } catch (Exception e) {
            data.add("SAVE FAIL");
        }
    }
}
