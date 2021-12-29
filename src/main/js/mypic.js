import { Button, Menu, Card, Layout, Row, Col, Modal, Form, Image, Input, Checkbox, Divider } from "antd";
import React, { Component } from "react";
import 'antd/dist/antd.css';
import axios from "axios";
import {PlusCircleTwoTone, UserOutlined, UploadOutlined, VideoCameraOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons'
const { Header, Content, Footer } = Layout;
import cookie from 'react-cookies';
import ParticlesBg from "particles-bg";
import Annotation from 'react-image-annotation'
import JSZip from "jszip";
import ShowLabel from "./showlabel";

class Mypic extends React.Component{
    constructor(){
        super()
        this.state = {
            cur_user_id: cookie.load("cur_user_id"),
            label_modal_appear: false,
            
            //拉取所有图片
            pic_list : [],
            //拉取所有标注
            mylabel_list: [],

            //标注相关
            annotations: [],
            annotation: {},
            pic_url_labling: "", 
            pic_id_labeling: 0,
            circle_pic_width: 0,
            circle_pic_height: 0,

            //制作任务或者导出数据的对应数组
            url_list: [],
            label_port: [],

            //视频相关
            video_name : "",
            frame_list: [],

            //阅览标注相关
            see_modal_appear: false,
            
        }
        console.log("当前帐号" + cookie.load("cur_user_id"))
        this.allpic_get()
        this.allmylabel_get()
        
    }

    exitlogin(){
        //清除当前账号
        cookie.remove('cur_user_id')
        window.location.href = '/login'
    }
    go_mytask(){
        window.location.href = '/mytask'
    }
    go_missionsquare(){
        window.location.href = '/missionsquare'
    }
    handleCancel(){
        this.setState({
            label_modal_appear:false,
            see_modal_appear: false
        })
    }
    // 12.23 20.33mochu
    // //创建任务
    // create_mission = (pic_id, pic_url) => {
    //     console.log(pic_id, pic_url)
    //     axios.post(`/create_mission`, {pictureid:pic_id, publisheraccount:this.state.cur_user_id, pictureurl:pic_url}).then(
    //         response => {
    //             console.log(response)
    //             console.log(response.data)
    //             //刚被创建任务的不能再被创建，刷新
    //             // this.allpic_get()
    //         }
    //     )
    // }
    //获取某账号的所有图片
    allpic_get = () => {
        axios.get(`/allpic_get/${this.state.cur_user_id}`).then(
            response => {
                console.log(response)
                console.log(response.data)
                this.setState({
                    pic_list: response.data
                })
            }
        )
    }
    //获取我的所有标注
    allmylabel_get = () => {
        axios.get(`/get_mylabel/${this.state.cur_user_id}`).then(
            response => {
                console.log(response.data)
                this.setState({
                    mylabel_list: response.data
                })
                
            }
        )
        
    }


    //显示标注框
    come_label = (pic_id, pic_url, pic_width, pic_height) => {
        this.setState({
            label_modal_appear: true,
            pic_url_labling:pic_url,
            circle_pic_width: pic_width,
            circle_pic_height:pic_height,
            pic_id_labeling:pic_id
        })
    }

    //标注工具原装
    onChange = (annotation) => {
        this.setState({ annotation })
    }
    //提交标注
    onSubmit = (annotation) => {
        const { geometry, data } = annotation

        this.setState({
            annotation: {},
            annotations: this.state.annotations.concat({
            geometry,
            data: {
                ...data,
                id: Math.random()
            }
            })
        })
        console.log(this.state.annotation)
        //上传标注，
        this.setState({
            label_modal_appear:false
        })
        console.log(this.state.annotation.data.text)
        axios.post(`/create_mylabel`, {owner:this.state.cur_user_id,
                pictureurl:this.state.pic_url_labling, picid:this.state.pic_id_labeling, 
                vx: this.state.annotation.geometry.x, vy: this.state.annotation.geometry.y,
            width: this.state.annotation.geometry.width, height: this.state.annotation.geometry.height, 
            picwidth:this.state.circle_pic_width, picheight:this.state.circle_pic_height,
            name: this.state.annotation.data.text})
        axios.get(`/setpic_label/${this.state.pic_id_labeling}`)
        window.location.href = '/mypic'
    }
    //添加或删除某一图片再备选栏
    add_pic = (e, path_url) => {
        console.log(`checked = ${e.target.checked}`)
        if (e.target.checked)
            this.state.url_list.push(path_url)
        else
            this.state.url_list.splice(this.state.url_list.indexOf(path_url), 1)
        console.log(this.state.url_list.length)
    }
    //添加或删除某一标注再备选栏
    add_mylabel = (e, id) => {
        if (e.target.checked)
            this.state.label_port.push(id)
        else
            this.state.label_port.splice(this.state.label_port.indexOf(id), 1)
        console.log(this.state.label_port.length)
    }
    new_mission = () => {
        if (this.state.url_list.length == 0)
            alert("请先选择若干张图片")
        else{
            let combine_url = this.state.url_list[0]
            var len = this.state.url_list.length
            for (var i=1;i<len;i++){
                combine_url = combine_url + "$" + this.state.url_list[i]
            }
            console.log(combine_url)
            axios.post(`/create_mission`, {publisheraccount:this.state.cur_user_id, pictureurl:combine_url}).then(
                response => {
                    console.log(response)
                    console.log(response.data)
                    //刚被创建任务的不能再被创建，刷新
                    // this.allpic_get()
                    this.setState({
                        url_list: []   //将临时列表清空
                    })
                }
            )
        }
        window.location.href = '/mypic'
    }
    

    render(){
        document.title = '我的图片';
        var myWidget = window.cloudinary.createUploadWidget({
            cloudName: 'yifengperson',
            uploadPreset: 'u9exwvaj'}, (error, result) => {
                if (!error && result && result.event === "success"){
                    console.log("new picture info:")
                    console.log(result.info)
                    console.log(result.info.secure_url)
                    var com_url = result.info.secure_url
                    var half_url = com_url.substring(com_url.indexOf('upload') + 7)
                    console.log(com_url)
                    console.log(half_url)
                    // {path:encodeURIComponent(result.info.secure_url)}
                    axios.post(`/newpic_up`, {path:encodeURIComponent(half_url), 
                                              owneraccount:this.state.cur_user_id,
                                              width: result.info.width, height:result.info.height} 
                            ).then(
                        response =>{
                            console.log(response)
                            this.allpic_get()
                        }
                    )
                    

                    // https://res.cloudinary.com/yifengperson/image/upload/v1640265230/m9gpzrusa03rjkwf5jyy.jpg
                }
            }
        )
        return (
            <>
            <Layout>
                <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
                    <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['1']}>
                        <Menu.Item key="1" >我的图片</Menu.Item>
                        <Menu.Item key="2" onClick={this.go_mytask.bind(this)}>和我相关的任务</Menu.Item>
                        <Menu.Item key="3" onClick={this.go_missionsquare.bind(this)}>任务市场</Menu.Item>
                        <Menu.Item key="4" onClick={this.exitlogin.bind(this)}style={{color:"gold", background:"#FF3333"}}>退出登录</Menu.Item>
                    </Menu>
                </Header>
                <Content className="site-layout" style={{ padding: '0 50px', marginTop: 64 }}>
                <div className="site-layout-background" style={{ padding: 24, minHeight: 500 }}>
                    <Card title="我的图片" style={{ width: 1390}}>
                    <Button onClick={()=>{myWidget.open();}} id="upload_widget" type="primary" shape="round"size="large" block icon={<PlusCircleTwoTone />}>上传图片</Button>
                    <br />
                    <br></br>
                    <Row>
                        <Col span={3}>图片编号</Col>
                        <Col span={6}>点击可预览</Col>
                        <Col span={3}>是否已经被我标注</Col>
                        {/* <Col span={3}>是否被设置为任务</Col> */}
                        <Col span={4}></Col>
                        <Col span={3}>选择</Col>
                    </Row>
                    <div><br></br></div>
                    {this.state.pic_list.map((item, idx) => {
                        return (
                        <Row>
                        <Col span={3}>{item['id']}</Col>
                        <Col span={6}>
                        <Image width={300}  src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(item['path'])} />
                        </Col>
                        <Col span={3}>{item['labelornot']? '已被标注':'未被标注'}</Col>
                        <Col span={4}>
                            <Button onClick={()=>this.come_label(item['id'], item['path'], item['width'], item['height'])} shape="round" style={{backgroundColor:"#fa8c16", color:"white",}}>自行标注</Button>
                        </Col>
                        <Col span={4}>
                            <Checkbox onChange={(e)=>this.add_pic(e, item['path'])}>勾选以添加至任务中</Checkbox>
                        </Col>      
                    </Row>
                        )
                    })}
                    <Button onClick={()=>this.new_mission()} type="primary" shape="round"size="large" block icon={<PlusCircleTwoTone />}>创建任务</Button>
<Modal title="标注" visible={this.state.label_modal_appear} footer={null} onCancel={()=>this.handleCancel()} >
<Annotation
        src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(this.state.pic_url_labling)}
        alt='获取图片失败'
        annotations={this.state.annotations}
        type={this.state.type}
        value={this.state.annotation}
        onChange={this.onChange}
        onSubmit={this.onSubmit}
        allowTouch
      />
</Modal>

                    </Card>
                    <br />
<Card title="上传视频并下载帧" style={{width: 1390}}>
<Input id="videoInput" type="file" accept="video/*" prefix={<UploadOutlined />} placeholder="选择视频"/>
  <br /><br />

  <video id="video" width="426" height="240" controls muted></video>
  <br />
  <canvas id="canvas" width="426" height="240"></canvas>

  <br /><br />

  <Button type="primary" id="download" block size="large">下载视频帧</Button>
</Card>
<br />
<Card title="我的标注" style={{width: 1390}}>
<Row>
<Col span={3}>原图片编号</Col>
<Col span={6}>点击以展示原图</Col>
<Col span={3}>标注名称</Col>
<Col span={6}>点击空白处，预览标注</Col>
<Col span={4}>添加导出/取消选择</Col>
</Row>
<Divider orientation="left">我的私有标注:</Divider>
{this.state.mylabel_list.map((item, idx) => {
    return (
<div>
<Row>
<Col span={3}>{item['picid']}</Col>
<Col span={6}>
<Image width={300}  src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(item['pictureurl'])} />
</Col>
<Col span={3}>{item['name']}</Col>
<Col span={6}>
    <ShowLabel item={item} idx={idx} size={300}/>
</Col>
<Col span={4}>
    <Checkbox onChange={(e)=>this.add_mylabel(e, item['id'])}>勾选以添加至导出结果中</Checkbox>
</Col>
</Row>
<br />
</div>
    )
})}
<Row>
<Col span={12}>
<Button onClick={()=>this.export_data()} type="primary" shape="round"size="large" block icon={<PlusCircleTwoTone />}>coco格式导出所选数据</Button><br />
</Col>
<Col span={12}>
<Button onClick={()=>this.export_xml()} type="primary" shape="round"size="large" block icon={<PlusCircleTwoTone />}>VOC格式导出所选数据</Button>
</Col>
</Row>
</Card>
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center', fontFamily: 'cursive', fontSize: 'large'}}>Presented By Tong Yuntze</Footer>
            </Layout>
            </>
        )
    }



    export_data = () => {
        if (this.state.label_port.length == 0){
            alert('请先选择若干张图片')
            return ;
        }
        axios({
            method: 'post',
            url: `/export_coco`,
            data: this.state.label_port,
            responseType: 'blob'
        }).then((res) => {
            console.log(res.data)
            const content = res.data;
            const blob = new Blob([content]);
            const fileName = "coco.zip";  //"myexport.json"
            const selfURL = window[window.webkitURL ? 'webkitURL' : 'URL'];
            let elink = document.createElement('a');
            if ('download' in elink) {
                elink.download = fileName;
                elink.style.display = 'none';
                elink.href = selfURL['createObjectURL'](blob);
                document.body.appendChild(elink);
                // 触发链接
                elink.click();
                console.log('click finish')
                selfURL.revokeObjectURL(elink.href);
                document.body.removeChild(elink)
            } else {
                navigator.msSaveBlob(blob, fileName);
            }
            // window.location.href = '/mypic'
        })
    }


    export_xml = () => {
        if (this.state.label_port.length == 0){
            alert('请先选择若干张图片')
            return ;
        }
        axios({
            method: 'post',
            url: `/export_xml`,
            data: this.state.label_port,
            responseType: 'blob'
        }).then((res) => {
            console.log(res.data)
            const content = res.data;
            const blob = new Blob([content]);
            const fileName = "voc.zip";
            const selfURL = window[window.webkitURL ? 'webkitURL' : 'URL'];
            let elink = document.createElement('a');
            if ('download' in elink) {
                elink.download = fileName;
                elink.style.display = 'none';
                elink.href = selfURL['createObjectURL'](blob);
                document.body.appendChild(elink);
                // 触发链接
                elink.click();
                console.log('click finish')
                selfURL.revokeObjectURL(elink.href);
                document.body.removeChild(elink)
            } else {
                navigator.msSaveBlob(blob, fileName);
            }
            // window.location.href = '/mypic'
        })
    }
    

    componentDidUpdate(){
        let videoName;
        let frames;
        const video = document.getElementById('video');
        const canvas = document.getElementById('canvas');
        const inputElement = document.getElementById('videoInput');

        // when user inputs video, load it in our video element
        inputElement.addEventListener('change', () => {
            frames = []; // clear existing frame data
            let fileList = inputElement.files;
            if(fileList.length > 0) {
                videoName = fileList[0].name.split('.')[0];
                video.src = URL.createObjectURL(fileList[0]);
                video.load();
            }
            console.log(videoName)
            this.setState({
                video_name: videoName,
            })
        });

        const ctx = canvas.getContext('2d');
        // when the video is playing draw each frame to canvas
        // then encode the canvas as a base64 png
        video.addEventListener('play', () => {
        function drawFrame() {
            if(!video.paused && !video.ended) {
                ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
                let img = canvas.toDataURL("image/jpg");
                frames.push(img);
                requestAnimationFrame(drawFrame);
            }
            
            // console.log(this.state.frame_list)
        }
        requestAnimationFrame(drawFrame);
        });


        const downloadbtn = document.getElementById('download')
        downloadbtn.addEventListener('click', () => {
            // download the encoded png files as a zip
            video.pause();
            if(frames && frames.length > 0) {
                console.log(frames.length + ' frames captured');
                let zip = new JSZip();
                let zipFilename = this.state.video_name + "Frames.zip";
            
                // save one frame for every 60 captures
                for(let i = 0; i < frames.length; i += 60) {
                    zip.file(i + ".jpg", frames[i].split(',')[1], {base64: true});
                    // zip.file(i + ".jpg", frames[i].base64, {base64: true});
                }
            
                zip.generateAsync({ type: 'blob' }).then(function (content) {
                    saveAs(content, zipFilename);
                });
            }
        });
    }



    
}


export default Mypic;

