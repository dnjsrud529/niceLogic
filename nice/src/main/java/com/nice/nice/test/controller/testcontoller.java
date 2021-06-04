package com.nice.nice.test.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class testcontoller {
    private String cookie;
    public List<String> data = new ArrayList<>();

    @RequestMapping(value = "/main.do", method = RequestMethod.GET)
    public String index(Model model) {
        //model.addAttribute("data", "Hello, Spring from IntelliJ! :)");
        data.add("");
        //model.addAttribute("data",data);
        System.out.println("main in!");
        return "main";
    }

    @RequestMapping(value = "/test")
    public ModelAndView test(){
        ModelAndView mav = new ModelAndView("main");
        mav.addObject("data","taesta");
        return mav;
    }

    @RequestMapping(value = "/naver.do")
    public String naver(){
        return "redirect:http://naver.com";
    }

    @RequestMapping(value = "/commit.do", method = RequestMethod.POST)
    public ModelAndView ModelAndView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("main");

        String name = request.getParameter("username");
        String password = request.getParameter("password");
        String project = request.getParameter("project");
        String filePath = request.getParameter("filepath");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username",name);
        params.add("password",password);
        params.add("REQUEST_KIND","API");

        HttpEntity entity = new HttpEntity(params,null);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/user/login/process", HttpMethod.POST, entity, String.class);
            cookie = responseEntity.getHeaders().get("SET-COOKIE").get(0);
            data.add(responseEntity.getStatusCode() + "\n" + responseEntity.getBody()+"\n\n");

            response.addHeader("SET-COOKIE", cookie);
            System.out.println(responseEntity.getStatusCode());
            System.out.println(responseEntity.getBody());
            if(anal(project,filePath)){
                resultChk(project);
            } else{

            }
            mav.addObject("data", data);
        } catch (Exception e){
            data.add("fail\n\n");
            mav.addObject("data",data);
        }
        return mav;
    }

    public boolean anal(String project, String filepath){

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie",cookie);

            List<String> files = new ArrayList<>();

            findFile(filepath,files);

            String analFiles = "";

            for(int i=0;i<files.size();i++){
                if(i!=files.size()-1)
                    analFiles += files.get(i)+",";
                else
                    analFiles += files.get(i);
            }

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            params.add("projectname",project);
            params.add("filelist",analFiles);
            HttpEntity entity = new HttpEntity(params,headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8080/api/analyzeCluster", HttpMethod.POST, entity, String.class);
            data.add(responseEntity.getStatusCode() + "\n" + responseEntity.getBody()+"\n\n");
            if(analCheck(project).contains("success")) {
                data.add("anal complete!");
                System.out.println(responseEntity.getStatusCode());
                System.out.println(responseEntity.getBody());
                return true;
            }
            else {
                data.add("anal fail!");
                System.out.println(responseEntity.getStatusCode());
                System.out.println(responseEntity.getBody());
                return false;
            }

        }catch (Exception e){
            data.add("anal fail\n\n");
            return false;
        }
    }

    @RequestMapping(value = "/result.do")
    public void resultChk(String project){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie",cookie);

            HttpEntity entity = new HttpEntity(null,headers);

            project = "test";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity responseEntity = restTemplate.exchange("http://localhost:8080/api/projects/" + project + "/fileresult", HttpMethod.GET, entity, String.class);
//            JSONParser parser = new JSONParser();
//            JSONObject jsonObject = (JSONObject) parser.parse(responseEntity.getBody().toString());

            System.out.println(responseEntity.getStatusCode()+", "+responseEntity.getBody());
        }catch (Exception e){
            data.add("get result fail");
        }
    }

    public class FileResult {
        String fileName;
        HashMap<Integer, Integer> risky;
        String url;

        public FileResult(String f, HashMap<Integer, Integer> r, String u) {
            fileName = f;
            risky = r;
            url = u;
        }

        public String getFileName() {
            return fileName;
        }

        public HashMap<Integer, Integer> getRisky() {
            return risky;
        }

        public String getUrl() {
            return url;
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
            if (status.contains("progressing") || status.contains("unziping") || status.contains("reserved")) {
                Thread.sleep(5000);
                status = analCheck(projectname);
            }
        }catch (Exception e){

        }

        return status;
    }

    public static void findFile(String path,List<String> filelist){
        File[] files = new File(path).listFiles();

        for(File f : files){
            if(f.isDirectory()) {
                findFile(f.getAbsolutePath(),filelist);
            } else {
                filelist.add(f.getAbsolutePath());
            }
        }
    }
}
