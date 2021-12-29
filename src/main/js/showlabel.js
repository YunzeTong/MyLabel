import React from "react";


export default class ShowLabel extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            item: props.item,
            idx: props.idx,
            size: props.size
        }
    }

    
    see_label = () => {
        console.log(this.state.item['pictureurl'])

        var img = new Image();
        img.src = "https://res.cloudinary.com/yifengperson/image/upload/" +decodeURIComponent(this.state.item['pictureurl']);
        console.log(img.width, img.height)

        let canvasHeight = Math.round(
            (this.state.size * Number(img.height)) / Number(img.width)
            // (400 * Number(this.state.item['picheight'])) / Number(this.state.item['picwidth'])
          );
        var myCanvas = document.getElementById("label_canvas"+this.state.idx);
        var myctx = myCanvas.getContext("2d");
        // console.log(myctx)
        console.log("ctx finish")
        

        img.onload = () => {
            myCanvas.setAttribute("width", this.state.size);
            myCanvas.setAttribute("height", canvasHeight); //重新设置画布的大小
            myctx.drawImage(img, 0, 0, this.state.size, canvasHeight);
            //矩形高亮
            let realX1 = Math.round(this.state.item['vx'] / 100 * this.state.size); //画布坐标x1
            let realY1 = Math.round(this.state.item['vy'] / 100 * canvasHeight);
            let realX2 = Math.round(realX1 + this.state.item['width'] / 100 * this.state.size);
            let realY2 = Math.round(realY1 + this.state.item['height'] / 100 * canvasHeight);
            //选中区域的宽高
            let width = realX2 - realX1;
            let height = realY2 - realY1;
            myctx.lineWidth = "2";// 线条的粗细
            myctx.strokeStyle = "red"; // 线条的颜色
            myctx.rect(realX1, realY1, width, height);
            myctx.stroke();// 最后，画线条，作用是描边————这句才是真正的画线！
          };
        
}
    


    render(){
        return(
            <canvas alt="点击" id={"label_canvas"+this.state.idx} onClick={this.see_label}></canvas>
        )
    }
}