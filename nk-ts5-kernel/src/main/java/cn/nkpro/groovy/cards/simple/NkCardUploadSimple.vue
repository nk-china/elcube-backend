<template>
    <nk-card>
        <nk-form :col="1">
            <nk-form-item title="文件">
                <a-upload accept    =".pdf,.doc,.docx,.xls,.xlsx,.wps,audio/*,video/*,image/*,text/plain "
                          class     ="uploader"
                          list-type="picture-card"
                          :action   ="config.host"
                          :headers  ="config.header"
                          :data     ="config.data"
                          :showUploadList="false"
                          :before-upload="beforeUpload"
                          @change="handleChange"
                >
                    <a-avatar :src="imgUrl" v-if="imgUrl" shape="square" :size="84"></a-avatar>
                    <template v-else>
                        <a-icon :type="progressStatus==='active' ? 'loading' : 'plus'" />
                        <div class="text">
                            上传
                        </div>
                    </template>
                </a-upload>
                <a-progress v-if="progressPercent!==undefined" :percent="progressPercent" :status="progressStatus" size="small" />
            </nk-form-item>
            <nk-form-item title="文件名">
                {{filename}}
            </nk-form-item>
            <nk-form-item title="URL">
                {{url}}
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
import Mixin from "Mixin";
export default {
    mixins:[new Mixin({})],
    created() {
    },
    data(){
        return {
            config: {},
            progressPercent:undefined,
            progressStatus:undefined,
            filename:undefined,
            url:undefined,
            imgUrl:undefined
        }
    },
    methods:{
        beforeUpload(file){
            return new Promise((resolve,reject)=>{
                this.$http.postJSON("/api/fs/init", {name:file.name,size:file.size,type:file.type})
                    .then(res=>{
                        if(file.size>1024*1024*30){
                            this.$message.error(`文件 ${file.name} 大小超过限制`);
                            reject();
                            return;
                        }
                        this.config = res.data;
                        resolve();
                    }).catch(res=>{
                        this.$message.error(res);
                        reject();
                    });
            })
        },
        handleChange(info){
            switch (info.file.status){
                case 'uploading':
                    this.progressPercent = Math.round(info.file.percent);
                    this.progressStatus  = 'active';
                    break;
                case 'error':
                    this.progressStatus  = 'exception';
                    this.$message.error(`文件 ${info.file.name} 上传失败`);
                    break;
                case 'done':
                    //为了演示效果，增加延时
                    setTimeout(()=>{
                        this.progressStatus  = 'success';
                        this.$message.success(`文件 ${info.file.name} 已上传`);
                        this.fileUploaded(info);
                    },3000);
                    break;
            }
        },
        fileUploaded(info){
            this.filename = info.file.name;
            this.url = info.file.response.url||(this.config.path+(this.config.filename||info.file.name));

            this.$http.get("/api/fs/download?url="+this.url).then(res=>this.imgUrl=res.data);
        },
    }
}
</script>

<style scoped>
    ::v-deep.uploader > .ant-upload {
        width: 100px;
        height: 100px;
        border: 1px dashed #d9d9d9;
        border-radius: 4px;
        text-align: center;
    }
    ::v-deep.uploader i{
        font-size: 32px;
        color: #999;
    }
    ::v-deep.uploader .text{
        margin-top: 8px;
        color: #666;
    }
</style>

<docs>
    - 这是一段文档
</docs>