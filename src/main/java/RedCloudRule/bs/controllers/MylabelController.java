package RedCloudRule.bs.controllers;
import RedCloudRule.bs.repositories.MylabelRepository;
import RedCloudRule.bs.models.Mylabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


@RestController
public class MylabelController {
    private MylabelRepository mylabelRepository;

    @Autowired
    public MylabelController(MylabelRepository mylabelRepository){
        this.mylabelRepository = mylabelRepository;
    }

    @GetMapping("/get_mylabel/{account}")
    public ResponseEntity<List<Mylabel>> get_mylabel(@PathVariable(value = "account") String acc){
        List<Mylabel> mylabel_list = this.mylabelRepository.findByowner(acc);
        return ResponseEntity.ok(mylabel_list);
    }

    @PostMapping("/create_mylabel")
    public boolean create_mylabel(@RequestBody Mylabel new_info){
        Mylabel new_mylabel = new Mylabel(new_info.getOwner(), new_info.getPictureurl(),
                                        new_info.getPicwidth(), new_info.getPicheight(),
                                        new_info.getVx(), new_info.getVy(), new_info.getWidth(),
                                        new_info.getHeight(), new_info.getPicid(), new_info.getName());
        this.mylabelRepository.save(new_mylabel);
        return true;
    }

    @PostMapping("/export_coco")
    public void export_coco(HttpServletRequest request, HttpServletResponse res, @RequestBody Long[] numberList) throws Exception{
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
            List<Mylabel> label_list = this.mylabelRepository.findByIdIn(numberList);
            System.out.println(label_list.size());

            for (int i=0;i<label_list.size();i++){
                String text = "[";
                String labelNum = "\"number\": " + i + ",";
                String labelName = "\n\t\t\"label name\": \"" + label_list.get(i).getName() + "\",";
                String picturewidth = "\n\t\t\"picture width\": " + label_list.get(i).getPicwidth() + ",";
                String pictureheight = "\n\t\t\"picture height\": " + label_list.get(i).getPicheight() + ",";
                String vxInfo = "\n\t\t\"vx\": " + label_list.get(i).getVx() + ",";
                String vyInfo = "\n\t\t\"vy\": " + label_list.get(i).getVy() + ",";
                String widthInfo = "\n\t\t\"width\": " + label_list.get(i).getWidth() + ",";
                String heightInfo = "\n\t\t\"height\": " + label_list.get(i).getHeight() + "\n\t}";
                // if (i == 0){
                text += "\n\t{\n\t\t"+ labelNum + labelName + picturewidth + pictureheight + vxInfo + vyInfo + widthInfo + heightInfo;
                // }
                // else{
                //     text += ",\n\t{\n\t\t"+ labelNum + picturewidth + pictureheight + vxInfo + vyInfo + widthInfo + heightInfo;
                // }
                text += "\n]";
                GenerateFile(text, i, "coco");
                // downloadPicture("E:\\bs\\rubbishdata\\data\\coco\\" + "train", label_list.get(i).getPictureurl(), i);
                downloadPicture("C:\\rubbishdata\\data\\coco\\" + "train", label_list.get(i).getPictureurl(), i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
            
    }

    @PostMapping("/export_xml")
    public void export_xml(HttpServletRequest request, HttpServletResponse res, @RequestBody Long[] numberList) throws Exception{
        // String rootpath = "E:\\bs\\rubbishdata\\data\\VOC_ROOT";
        String rootpath = "C:\\rubbishdata\\data\\VOC_ROOT";
        GenerateXml(numberList);

        System.out.println("生成xml结束");

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

    public void GenerateXml(Long[] numberList) throws Exception{
        List<Mylabel> label_list = this.mylabelRepository.findByIdIn(numberList);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            int i = 0;
            for (Mylabel label: label_list){
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
                width.setTextContent(label.getPicwidth()+"");
                size.appendChild(width);

                Element height = document.createElement("height");
                height.setTextContent(label.getPicheight()+"");
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

                double bndboxWidth = label.getWidth() * label.getPicwidth() / 100;
                double bndboxHeight = label.getHeight() * label.getPicheight() / 100;

                double minx = label.getVx() * label.getPicwidth() / 100;
                double miny = label.getVy() * label.getPicheight() / 100;
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
                // downloadPicture("E:\\bs\\rubbishdata\\data\\VOC_ROOT\\" + "images", label.getPictureurl(), i);
                downloadPicture("C:\\rubbishdata\\data\\VOC_ROOT\\" + "images", label.getPictureurl(), i);
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
            // sfile = new File(path + "\\" + i + ".json");
            sfile = new File(annotation + "\\" + i + ".json");
        } else {
            // sfile = new File(path + "\\" + i + ".xml");
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
