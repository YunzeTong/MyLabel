import { Button, Menu, Card, Layout, Row, Col, Modal, Form, Image, Divider } from "antd";
import React, { Component } from "react";
import 'antd/dist/antd.css';
import ParticlesBg from "particles-bg";
import axios from "axios";
const { Header, Content, Footer } = Layout;
import cookie from 'react-cookies'

import Annotation from 'react-image-annotation'
import {PlusCircleTwoTone} from '@ant-design/icons'
import ShowLabel from "./showlabel";


class Mytask extends React.Component{
    constructor(){
        super()
        this.state = {
            cur_user_id: cookie.load("cur_user_id"),
            //以下两个claimer暂时没用到
            claimer_modal: false,                   
            claimer_list: ['user2', 'user3', 'user4'],

            my_claim_list: [],
            my_publish_unfin_list: [],
            examine_list: [],       //待审核的标注
            finish_list: [],         //已经通过的标注

            //当进行标注时需要间接传递的量
            pic_url_labling: "",     //这个其实一直是明文
            label_pic_modal: false,
            label_publisher: "",
            label_task: 0,
            label_mission_id: 0,

            //原装标注所需
            annotations: [],
            annotation: {},


        }
        // console.log("当前帐号"+this.state.cur_user_id)
        this.initialize_list();
    }

    go_mypic = () =>{
        window.location.href = '/mypic'
    }
    go_missionsquare = () =>{
        window.location.href = '/missionsquare'
    }
    exitlogin(){
        //清除当前账号
        cookie.remove('cur_user_id')
        window.location.href = '/login'
    }
    //取消掉所有的modal显示
    cancel_modal = () => {
        this.setState({
            label_pic_modal:false
        })
    }

    //初始化任务列表
    initialize_list = () => {
        this.get_myclaim();
        // this.get_mypublish();
        this.get_examine();
        this.get_finish();
        console.log("init finish")
    }
    //获得我认领的所有任务
    get_myclaim = () => {
        axios.get(`/get_myclaim/${this.state.cur_user_id}`).then(
            response => {
                // console.log(response.data)
                this.setState({
                    my_claim_list: response.data
                })
            }
        )
    }
    //获得我发布的未完成的任务
    get_mypublish = () =>{
        axios.get(`/get_mypublish/${this.state.cur_user_id}`).then(
            response => {
                // console.log(response.data)
                this.setState({
                    my_publish_unfin_list: response.data
                })
            }
        )
    }

    //获得待审核的任务（标注）
    get_examine = () => {
        axios.get(`/get_label/${this.state.cur_user_id}/${false}`).then(
            response => {
                console.log(response.data)
                this.setState({
                    examine_list: response.data.labelbatch
                })
            }
        )
    }
    //获得已经可以导出的任务
    get_finish = () =>{
        axios.get(`/get_label/${this.state.cur_user_id}/${true}`).then(
            response => {
                // console.log(response.data)
                this.setState({
                    finish_list: response.data.labelbatch
                })
            }
        )
    }

    //更新task
    upload_task = () =>{
        console.log(this.state.annotation.data.text)
        //新建标注
        axios.post(`/create_label`, {taskid: this.state.label_task,
            claimeraccount: this.state.cur_user_id, passornot: false,
            publisheraccount:this.state.label_publisher, missionid: this.state.label_mission_id,
            pictureurl: encodeURIComponent(this.state.pic_url_labling), vx: this.state.annotation.geometry.x,
            vy: this.state.annotation.geometry.y, width: this.state.annotation.geometry.width,
            height: this.state.annotation.geometry.height, name:this.state.annotation.data.text}).then(
            response => {
                console.log(response.data)
            }
        )
        //更新task表
        axios.get(`/update_task/${true}/${this.state.label_task}/${0}`).then(
            response => {
                console.log(response.data)
                if (response.data){alert("更新状态完成")}
                else{alert("更新状态出错")}
            }
        )
    }

    //对标注不予通过
    reject_label = (label_id, task_id) => {
        //删除标注
        axios.delete(`/delete_label/${label_id}`).then(
            response => {
                console.log(response.data)
            }
        )
        //更新task表为未完成
        axios.get(`/update_task/${false}/${task_id}/${1}`).then( //未完成, task, 被拒
            response => {
                if (response.data){alert("更新状态完成")}
                else{alert("更新状态出错")}
            }
        )
        window.location.href = '/mytask'
    }

    //通过标注
    pass_label = (label_id, task_id, pic_url) => {
        console.log(task_id, label_id)
        //修改标注为通过
        axios.get(`/pass_label/${label_id}`).then(
            response => {
                console.log(response.data)
            }
        )
        //更新task表为已经完成
        axios.get(`/update_task/${true}/${task_id}/${2}`).then(
            response => {
                if (response.data){alert("更新状态完成")}
                else{alert("更新状态出错")}
            }
        )
        
        window.location.href = '/mytask'
    }

    delete_label = (id, task_id) => {
        console.log("labelid:" + id)
        console.log("taskid"+task_id)
        //删除label
        axios.delete(`/delete_label/${id}`).then(
            response => {
                console.log(response.data)
            }
        )
        //删除label对应的task
        axios.delete(`/delete_task/${task_id}`).then(
            response => {
                console.log(response.data)
            }
        )


        window.location.href = '/mytask'
        
    }

    //前往标注，设置图片，出现modal
    //此时的pic_url是decode过后的明文
    come_label = (mission_id, label_pub, pic_url, task_id) => {
        console.log("出现modal")
        this.setState({
            label_pic_modal: true,
            pic_url_labling: pic_url,
            label_mission_id: mission_id,
            label_publisher: label_pub,
            label_task: task_id
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
        // console.log(this.state.annotation)
        // console.log(this.state.annotations)
        //更新task
        this.upload_task()
        this.setState({
            label_pic_modal:false
        })
        window.location.href = "/mytask"
    }


    delete_task = (mission_id) => {
        var task_list = this.state.my_claim_list
        for (var j=0; j<task_list.length; j++){
            if (task_list[j]['missionid'] == mission_id){
                axios.delete(`/delete_task/${task_list[j]['id']}`)
                axios.delete(`/delete_task_label/${task_list[j]['id']}`)
                console.log(task_list[j]['missionid'])
            }
        }
        window.location.href = "/mytask"
        
    }

    render(){
        document.title = "与我相关的任务"

        return(
            <>
            <Layout>
                <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
                    <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['2']}>
                        <Menu.Item key="1" onClick={this.go_mypic}>我的图片</Menu.Item>
                        <Menu.Item key="2" >和我相关的任务</Menu.Item>
                        <Menu.Item key="3" onClick={this.go_missionsquare}>任务市场</Menu.Item>
                        <Menu.Item key="4" onClick={this.exitlogin.bind(this)}style={{color:"gold", background:"#FF3333"}}>退出登录</Menu.Item>
                    </Menu>
                 </Header>
                <Content className="site-layout" style={{ padding: '0 50px', marginTop: 64 }}>
                    <div className="site-layout-background" style={{ padding: 24, minHeight: 500 }}>
                    <Card title="我认领的任务" style={{ width: 1390}}>
                    <div><br></br></div>
                    <Row>
                        <Col span={3}>任务编号</Col>
                        <Col span={6}>点击可预览</Col>
                        <Col span={3}>发布者</Col>
                        <Col span={3}>状态</Col>
                    </Row>
                    <br/>
                    {this.state.my_claim_list.map((item, idx) => {
                return (<div>
                <Row>
                <Col span={3}>{item['missionid']}</Col>
                <Col span={6}>
                <Image width={300} src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(item['pictureurl'])} />
                </Col>
                <Col span={3}>{item['publisheraccount']}</Col>
                <Col span={3}>{item['state']==2?"审核通过": item['state'] == 1?"上次提交被拒绝":item['labelornot']?"待审核":"未标注"}</Col>
                <Col span={4}>
                    <Button onClick={()=>this.come_label( item['missionid'], item['publisheraccount'],
                                        decodeURIComponent(item['pictureurl']) , item['id'] )} 
                    disabled={item['labelornot']} shape="round" type="primary">标注该图片</Button>
                </Col>
                <Col span={4}>
                    <Button onClick={()=>this.delete_task(item['missionid'])} type="danger" shape="round">放弃并删除该任务</Button>
                </Col>
                </Row>
                <br/></div>)
                    })}
<Modal title="标注" visible={this.state.label_pic_modal} footer={null} onCancel={()=>this.cancel_modal()} >
<Annotation
        src={"https://res.cloudinary.com/yifengperson/image/upload/"+this.state.pic_url_labling}
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
                    <Card title="我发布的待审核任务" style={{ width: 1390}}>
                    <div><br></br></div>
                    <Row>
                        <Col span={3}>此份task编号</Col>
                        <Col span={2}>标注者</Col>
                        <Col span={6}>点击可预览</Col>
                        <Col span={6}>是否给予通过</Col>
                        <Col span={4}>点击空白处预览标注情况</Col>
                    </Row>
                    <div><br></br></div>
                    {this.state.examine_list.map((item, idx) => {
    return (
<div>
<Divider orientation="left">mission {item[0]['missionid']}:</Divider>
        {item.map((inneritem, index) => {
            return (
<div><Row>
<Col span={3}>{inneritem['taskid']}</Col>
<Col span={2}>{inneritem['claimeraccount']}</Col>
<Col span={6}>
<Image src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(inneritem['pictureurl'])} width={300}></Image>
</Col>
<Col span={3}>
<Button onClick={()=>this.pass_label(inneritem['id'], inneritem['taskid'], inneritem['pictureurl'])} shape="round" style={{backgroundColor:"#52c41a", color:"white"}}>审核通过</Button>
</Col>
<Col span={3}>
<Button onClick={()=>this.reject_label(inneritem['id'], inneritem['taskid'])} shape="round" type="danger">审核驳回</Button>
</Col>
<Col span={4}>
<ShowLabel item={inneritem} idx={idx + "check"+index} size={300} />
</Col>
</Row><br /></div>
            )
        })
    }
    </div>
    )
    
                    })}
                    </Card>

                    <br />

                    <Card title="我发布的已经结束的任务" style={{ width: 1390}}>
                    <div><br></br></div>
                    <Row>
                        <Col span={3}>标注编号</Col>
                        <Col span={3}>最终标注者</Col>
                        <Col span={6}>点击可预览原图</Col>
                        <Col span={4}>删除该任务</Col>
                        <Col span={4}>点击空白处预览标注情况</Col>
                    </Row>
                    <div><br></br></div>

{this.state.finish_list.map((item, idx) => {
    return (
<div>
<Divider orientation="left">mission {item[0]['missionid']}: {item.length} pictures</Divider>
{item.map((inneritem,index) => {
return (
<div><Row>
<Col span={3}>{inneritem['id']}</Col>
<Col span={3}>{inneritem['claimeraccount']}</Col>
<Col span={6}>
<Image width={300} src={"https://res.cloudinary.com/yifengperson/image/upload/"+decodeURIComponent(inneritem['pictureurl'])} />
</Col>
<Col span={4}>
<Button onClick={()=>this.delete_label(inneritem['id'], inneritem['taskid'])} shape="round" type="danger">删除标注</Button>
</Col>
<Col span={4}>
    <ShowLabel item={inneritem} idx={idx+"fin"+index} size={300} />
</Col>
</Row>
<br /></div>
            )
        })
    }
<br />
<Row>
<Col span={12}>
<Button onClick={()=>this.export_data(item)} type="primary" shape="round" block icon={<PlusCircleTwoTone />}>导出{item[0]['missionid']}号任务的coco标注</Button>
</Col>
<Col span={12}>
<Button onClick={()=>this.export_voc(item)} type="primary" shape="round" block icon={<PlusCircleTwoTone />}>导出{item[0]['missionid']}号任务的voc标注</Button>
</Col>
</Row>
    </div>
    )
    
})}
                    </Card>
                    
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center', fontFamily: 'cursive', fontSize:'large'}}>Presented By Tong Yuntze</Footer>
            </Layout>
            <ParticlesBg type="custom" bg={true} />
            </>
        )
    }

    export_voc = (item) => {
        console.log("start to download")
        var num = item.length
        var id_list = []
        for (var i=0;i<num;i++){
            id_list.push(item[i]['id'])
        }
        console.log(id_list)

        axios({
            method: 'post',
            url: `/xml_export`,
            data: id_list,
            responseType: 'blob'
        }).then((res) => {
            console.log(res.data)
            const content = res.data;
            const blob = new Blob([content]);
            const fileName = "voc.zip"
            const selfURL = window[window.webkitURL ? 'webkitURL' : 'URL'];
            let elink = document.createElement('a');
            if ('download' in elink) {
                elink.download = fileName;
                elink.style.display = 'none';
                elink.href = selfURL['createObjectURL'](blob);
                document.body.appendChild(elink);
                // 触发链接
                elink.click();
                selfURL.revokeObjectURL(elink.href);
                document.body.removeChild(elink)
            } else {
                navigator.msSaveBlob(blob, fileName);
            }
        })
    }    

    export_data = (item) => {
        console.log("start to download")
        var num = item.length
        var id_list = []
        for (var i=0;i<num;i++){
            id_list.push(item[i]['id'])
        }
        // console.log(id_list)

        axios({
            method: 'post',
            url: `/coco_export`,
            data: id_list,
            responseType: 'blob'
        }).then((res) => {
            // console.log(res.data)
            const content = res.data;
            const blob = new Blob([content]);
            const fileName = "coco.zip"
            const selfURL = window[window.webkitURL ? 'webkitURL' : 'URL'];
            let elink = document.createElement('a');
            if ('download' in elink) {
                elink.download = fileName;
                elink.style.display = 'none';
                elink.href = selfURL['createObjectURL'](blob);
                document.body.appendChild(elink);
                // 触发链接
                elink.click();
                selfURL.revokeObjectURL(elink.href);
                document.body.removeChild(elink)
            } else {
                navigator.msSaveBlob(blob, fileName);
            }
        })
    }
}


export default Mytask
