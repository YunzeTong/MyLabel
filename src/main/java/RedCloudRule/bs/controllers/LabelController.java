package RedCloudRule.bs.controllers;
import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.repositories.LabelRepository;
import RedCloudRule.bs.repositories.PictureRepository;
import RedCloudRule.bs.models.Label;
import RedCloudRule.bs.models.Labelarr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RestController
public class LabelController {
    private LabelRepository labelRepository;
    private PictureRepository pictureRepository;

    @Autowired
    public LabelController(LabelRepository labelRepository, PictureRepository pictureRepository){
        this.labelRepository = labelRepository;
        this.pictureRepository = pictureRepository;
    }

    //新建标注,这里的taskid实际上是mission的
    @PostMapping("/create_label")
    public boolean create_label(@RequestBody Label new_info){
        Label new_label = new Label(new_info.getTaskid(), new_info.getClaimeraccount(),
                        new_info.getPublisheraccount(), new_info.getPictureurl(),
                        new_info.getVx(), new_info.getVy(), new_info.getWidth(),
                        new_info.getHeight(), new_info.getMissionid(), new_info.getName());
        //这里没有判断，先这么写
        this.labelRepository.save(new_label);
        return true;
    }

    //标注被拒绝掉，进行删除，已经验证正确
    @DeleteMapping("/delete_label/{id}")
    public ResponseEntity<Void> delete_label(@PathVariable(value = "id") Long id){
        Label label =this.labelRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Label not found")
        ); 

        this.labelRepository.delete(label);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_task_label/{id}")
    public ResponseEntity<Void> delete_task_label(@PathVariable(value = "id") Long id){
        List<Label> label_list =this.labelRepository.findBytaskid(id);
        System.out.println(label_list.size());

        for (Label l: label_list){
            this.labelRepository.delete(l);
        }
        return ResponseEntity.ok().build();
    }

    //标注被通过,这个函数仿照task里面的update_task写的
    @GetMapping("/pass_label/{id}")
    public boolean pass_label(@PathVariable(value = "id") Long id){
        System.out.println(id);
        this.labelRepository.findById(id)
            .map(label -> {
                label.setPass();
                return this.labelRepository.save(label);
            }
        );
        Label change_label = this.labelRepository.findById(id).get();  //这个是看网上说法写的，不确定对
        System.out.println("原则上应该为true:");
        System.out.println(change_label.getPass());
        return true;

    }



    // //这个还没有检测
    // //获得我发布的待审核（fasle)标注或者已完成（true)标注（task label合一了）
    // @GetMapping("/get_label/{account}/{judge}")
    // public ResponseEntity<List<Label>> get_examine(@PathVariable(value = "account") String acc,
    //                                                 @PathVariable(value = "judge") boolean judge){
    //     System.out.println(acc);
    //     List<Label> label_list = this.labelRepository.getwithPubandJudge(acc, judge);
    //     return ResponseEntity.ok(label_list);
    // }


    @GetMapping("/get_label/{account}/{judge}")
    public ResponseEntity<Labelarr> get_examine(@PathVariable(value = "account") String acc,
                                                    @PathVariable(value = "judge") boolean judge){
        // System.out.println(acc);
        List<Long> missionid_list = this.labelRepository.separateMissionid(acc, judge);
        System.out.printf("找到%d个mission\n", missionid_list.size());
        
        Labelarr result = new Labelarr();

        for (Long single_id: missionid_list){
            System.out.println(single_id);
            List<Label> label_batch = this.labelRepository.getwithPubandJudgeandMission(acc, judge, single_id);
            result.pushnew(label_batch);
        }
        
        return ResponseEntity.ok(result);
    }


    @PostMapping("/coco_export")
    public void coco_export(HttpServletRequest request, HttpServletResponse res, @RequestBody Long[] numberList) throws Exception{
        // String rootpath = "E:\\bs\\rubbishdata\\data\\coco";
        String rootpath = "C:\\rubbishdata\\data\\coco";
        GenerateJson(numberList);

        System.out.println("生成json结束");

        res.setCharacterEncoding("UTF-8");
        String realFileName = "new.zip";
        res.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        res.setContentType("application/octet-stream;charset=UTF-8");
        // res.addHeader("Content-Length", String.valueOf(text.length()));
        try {
            res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(realFileName.trim(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("header complete");

        BufferedOutputStream buff = null;
        ServletOutputStream outStr = null;
        try {
            outStr = res.getOutputStream();
            buff = new BufferedOutputStream(outStr);
            toZip(rootpath, buff, true);
            // buff.write(text.getBytes("UTF-8"));
            buff.flush();
            buff.close();
            deleteDir(rootpath);
        } catch (Exception e) {
            System.out.println("导出文件文件出错");
            e.printStackTrace();
        } finally {
            try {
                buff.close();
                outStr.close();
            } catch (Exception e) {
                System.out.println("关闭流对象出错");
                e.printStackTrace();
            }

        }
    }

    public void GenerateJson(Long[] numberList) throws Exception{
        try {
            PictureController controlpic = new PictureController(this.pictureRepository);

            List<Label> label_list = this.labelRepository.findByIdIn(numberList);
            System.out.println(label_list.size());

            for (int i=0;i<label_list.size();i++){
                String text = "[";
                int picwidth = controlpic.widthfind(label_list.get(i).getPictureurl());
                int picheight = controlpic.heightfind(label_list.get(i).getPictureurl());
                String labelNum = "\"number\": " + i + ",";
                String labelName = "\n\t\t\"label name\": \"" + label_list.get(i).getName() + "\",";
                String oralwidth = "\n\t\t\"picture width\": " + picwidth + ",";
                String oralheight = "\n\t\t\"picture height\": " + picheight + ",";
                String vxInfo = "\n\t\t\"vx\": " + label_list.get(i).getVx() + ",";
                String vyInfo = "\n\t\t\"vy\": " + label_list.get(i).getVy() + ",";
                String widthInfo = "\n\t\t\"width\": " + label_list.get(i).getWidth() + ",";
                String heightInfo = "\n\t\t\"height\": " + label_list.get(i).getHeight() + "\n\t}";
                text += "\n\t{\n\t\t"+ labelNum + labelName + oralwidth + oralheight +
                    vxInfo + vyInfo + widthInfo + heightInfo;
                
                text += "\n]";
                GenerateFile(text, i, "coco");
                // downloadPicture("E:\\bs\\rubbishdata\\data\\coco\\" + "pictures", label_list.get(i).getPictureurl(), i);
                downloadPicture("C:\\rubbishdata\\data\\coco\\" + "pictures", label_list.get(i).getPictureurl(), i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
            
    }

    @PostMapping("/xml_export")
    public void xml_export(HttpServletRequest request, HttpServletResponse res, @RequestBody Long[] numberList) throws Exception{
        // String rootpath = "E:\\bs\\rubbishdata\\data\\VOC_ROOT";
        String rootpath = "C:\\rubbishdata\\data\\VOC_ROOT";
        GenerateXml(numberList);

        System.out.println("生成xml结束");

        res.setCharacterEncoding("UTF-8");
        String realFileName = "new.zip";
        res.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        res.setContentType("application/octet-stream;charset=UTF-8");
        try {
            res.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(realFileName.trim(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("header complete");

        BufferedOutputStream buff = null;
        ServletOutputStream outStr = null;
        try {
            outStr = res.getOutputStream();
            buff = new BufferedOutputStream(outStr);
            toZip(rootpath, buff, true);
            buff.flush();
            buff.close();
            deleteDir(rootpath);
        } catch (Exception e) {
            System.out.println("导出文件文件出错");
            e.printStackTrace();
        } finally {
            try {
                buff.close();
                outStr.close();
            } catch (Exception e) {
                System.out.println("关闭流对象出错");
                e.printStackTrace();
            }
        }
    }

    public void GenerateXml(Long[] numberList) throws Exception{
        List<Label> label_list = this.labelRepository.findByIdIn(numberList);
        PictureController contropic = new PictureController(this.pictureRepository);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            int i = 0;
            int picwidth, picheight;
            for (Label label: label_list){
                picwidth = contropic.widthfind(label_list.get(i).getPictureurl());
                picheight = contropic.heightfind(label_list.get(i).getPictureurl());
                String xmlString = "";
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                document.setXmlStandalone(true);
                Element Annotation = document.createElement("Annotation");
                document.appendChild(Annotation);

                Element folder = document.createElement("folder");
                folder.setTextContent("VOC_ROOT");
                Annotation.appendChild(folder);

                Element filename = document.createElement("filename");
                filename.setTextContent(label.getId() + ".jpg");
                Annotation.appendChild(filename);

                Element size = document.createElement("size");
                Annotation.appendChild(size);

                Element width = document.createElement("width");
                width.setTextContent(picwidth+"");
                size.appendChild(width);

                Element height = document.createElement("height");
                height.setTextContent(picheight+"");
                size.appendChild(height);

                Element depth = document.createElement("depth");
                depth.setTextContent("3");
                size.appendChild(depth);

                Element segmented = document.createElement("segmented");
                segmented.setTextContent("0");
                Annotation.appendChild(segmented);

                //object部分
                Element object = document.createElement("object");
                Annotation.appendChild(object);

                Element name = document.createElement("name");
                name.setTextContent(label.getName());
                object.appendChild(name);

                Element pose = document.createElement("pose");
                pose.setTextContent("Unspecified");
                object.appendChild(pose);

                Element truncated = document.createElement("truncated");
                truncated.setTextContent("0");
                object.appendChild(truncated);

                Element difficult = document.createElement("difficult");
                difficult.setTextContent("difficult");
                object.appendChild(difficult);

                Element bndbox = document.createElement("bndbox");
                object.appendChild(bndbox);

                double bndboxWidth = label.getWidth() * picwidth / 100;
                double bndboxHeight = label.getHeight() * picheight / 100;

                double minx = label.getVx() * picwidth / 100;
                double miny = label.getVy() * picheight / 100;
                double maxx = minx + bndboxWidth;
                double maxy = miny + bndboxHeight;

                Element xmin = document.createElement("xmin");
                xmin.setTextContent((int)minx + "");
                bndbox.appendChild(xmin);

                Element ymin = document.createElement("ymin");
                ymin.setTextContent((int)miny + "");
                bndbox.appendChild(ymin);

                Element xmax = document.createElement("xmax");
                xmax.setTextContent((int)maxx + "");
                bndbox.appendChild(xmax);

                Element ymax = document.createElement("ymax");
                ymax.setTextContent((int)maxy + "");
                bndbox.appendChild(ymax);

                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource domSource = new DOMSource(document);
    
                // xml transform String
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                transformer.transform(domSource, new StreamResult(bos));
                xmlString = bos.toString();
                System.out.println(xmlString);
                GenerateFile(xmlString, i, "VOC_ROOT");
                // downloadPicture("E:\\bs\\rubbishdata\\data\\VOC_ROOT\\" + "pictures", label.getPictureurl(), i);
                downloadPicture("C:\\rubbishdata\\data\\VOC_ROOT\\" + "pictures", label.getPictureurl(), i);
                i++;
            }
            buildImgSets(i);

        } catch(Exception e){
            e.printStackTrace();
        } 
    }

    public void downloadPicture(String path, String origin_url, int i){
        File folder = new File(path);
        if (!folder.exists()){   //建立picture文件夹
            folder.mkdir();
        }

        path += "\\" + i + ".jpg";
        System.out.println(path);

        try {
            origin_url = "https://res.cloudinary.com/yifengperson/image/upload/" + java.net.URLDecoder.decode(origin_url, "UTF-8");
        } catch (UnsupportedEncodingException u){
            System.out.println("解码失败");
            return;
        }

        URL url = null;
        try {
            url = new URL(origin_url);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void GenerateFile(String content, int i, String type){
        // String path = "E:\\bs\\rubbishdata\\data\\" + type; 
        String path = "C:\\rubbishdata\\data\\" + type; 
        File f = new File(path);
        if (!f.exists()){
            f.mkdir();
        }

        String annotation = path + "\\" + "annotations";
        File f_ann = new File(annotation);
        if (!f_ann.exists()){
            f_ann.mkdir();
        }

        File sfile;
        if (type.equals("coco")){
            sfile = new File(annotation + "\\" + i + ".json");
        } else {
            sfile = new File(annotation + "\\" + i + ".xml");
        }
        
        
        try {
            FileOutputStream of = new FileOutputStream(sfile);
            of.write(content.getBytes("UTF-8"));
            of.flush();
            of.close();
        }catch (Exception o){
            o.printStackTrace();
        }
        
    }

    public void buildImgSets(int num){
        // String path = "E:\\bs\\rubbishdata\\data\\VOC_ROOT\\ImageSets";
        String path = "C:\\rubbishdata\\data\\VOC_ROOT\\ImageSets";
        File imagesetFolder = new File(path);
        if (!imagesetFolder.exists()){
            imagesetFolder.mkdir();
        } else {
            deleteDir(path);
            imagesetFolder.mkdir();
        }

        path += "\\Main";
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdir();
        } else {
            deleteDir(path);
            folder.mkdir();
        }

        path += "\\train.txt";
        File f = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            for (int i=0;i<num;i++){
                fos.write((i + "\n").getBytes("UTF-8"));
                fos.flush();
            }
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**删掉传入路径：文件夹及以下的所有内容 */
    public void deleteDir(String path){
        File dir = new File(path);
        if (!dir.exists()){
            System.out.println("文件夹不存在");
        } else {
            File[] allFiles = dir.listFiles();
            for (int i =0; i < allFiles.length;i++){
                if (allFiles[i].isDirectory()){
                    deleteDir(allFiles[i].getAbsolutePath());
                } else {
                    allFiles[i].delete();
                }
                // if (!allFiles[i].exists()){
                //     System.out.println("文件不存在");
                // } else {
                //     allFiles[i].delete();
                // }
            }
            dir.delete();
            System.out.println("删除文件结束");
        }
    }

    private static final int  BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException{

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 压缩成ZIP 方法2
     * @param srcFiles 需要压缩的文件列表
     * @param out 	        压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos		 zip输出流
     * @param name		 压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }

                }
            }
        }
    }

}