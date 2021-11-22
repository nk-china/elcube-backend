<template>
    <div style="width: 90%;">
        <div  v-if="!editMode" style="display: flex">
            <template v-if="inputOptions.listType==='picture-card'">
                <div v-for="item in fileList" :key="item.uid" class="avatar-border" @click="handlePreview(item)">
                    <a-avatar :src="item.url" shape="square" :size="84"></a-avatar>
                </div>
            </template>
            <template v-else>
                <a v-for="item in fileList" :key="item.uid" @click="handlePreview(item)">{{item.name}}</a>
            </template>
        </div>
        <div v-else style="display: flex;align-items: center;">
            <a-upload v-if="editMode"
                      :accept    ="inputOptions.accept"
                      class     ="uploader"
                      :default-file-list="fileList"
                      :list-type="inputOptions.listType"
                      :action   ="config.host"
                      :headers  ="config.header"
                      :data     ="config.data"
                      :showUploadList="true"
                      :before-upload="beforeUpload"
                      @change="handleChange"
                      @preview="handlePreview"
            >
                <div v-if="fileList.length === 0 && inputOptions.listType==='picture-card'">
                    <a-icon :type="progressStatus==='active' ? 'loading' : 'plus'"></a-icon>
                    <div class="ant-upload-text">
                        上传
                    </div>
                </div>
                <div v-else-if="fileList.length === 0" style="margin-left: 10px;">
                    <a>上传</a>
                </div>
            </a-upload>
        </div>
        <a-progress v-if="progressPercent!==undefined" :percent="progressPercent" :status="progressStatus" size="small" />
        <a-modal :visible="previewVisible" :footer="null" @cancel="previewVisible = false">
            <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
        <a v-show="false" ref="link"></a>
    </div>
</template>

<script>

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

export default {
    props:{
        value: {},
        editMode: {
            type: Boolean,
            default:false
        },
        inputOptions: {
            type:Object,
            default(){
                return {}
            }
        }
    },
    data(){
        return {
            config: {},
            progressPercent:undefined,
            progressStatus:undefined,

            fileList:[],
            downloadUrl:undefined,

            previewVisible: false,
            previewImage: '',
        }
    },
    mounted(){
        this.nk$editModeChanged(this.editMode);
    },
    methods:{
        nk$editModeChanged(){
            if(this.value){
                this.fileList = [];
                if(this.inputOptions.listType==='picture-card'){
                    this.$http.get("/api/fs/download?url="+this.value.path).then(res=>{
                        this.fileList.push({
                            uid : -1,
                            name: this.value.name,
                            url:  res.data,
                            path: this.value.path,
                        })
                    });
                }else{
                    this.fileList.push({
                        uid : -1,
                        name: this.value.name,
                        path: this.value.path,
                    })
                }
            }
            this.progressPercent = undefined
        },
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
                case 'down':
                case 'done':
                    //为了演示效果，增加延时
                    setTimeout(()=>{
                        this.progressStatus  = 'success';
                        this.$message.success(`文件 ${info.file.name} 已上传`);
                        this.fileUploaded(info);
                    },3000);
                    break;
                case 'removed':
                    this.fileList = info.fileList;
                    this.$emit('input',undefined);
                    break;
            }
        },
        fileUploaded(info){

            info.file.path = info.file.response.url||(this.config.path+(this.config.filename||info.file.name));
            this.fileList.push(info.file);

            this.$emit('input',{
                path: info.file.path,
                name: info.file.name,
            });
        },
        async handlePreview(file) {

            let url = undefined;
            if(!file.path){
                url = await getBase64(file.originFileObj);
            }else{
                const res = await this.$http.get("/api/fs/download?url=" + file.path);
                url = res.data;
            }

            if (this.inputOptions.listType === 'picture-card') {
                this.previewImage = url;
                this.previewVisible = true;
            } else {
                this.$refs.link.href=url;
                this.$refs.link.click();
            }
        },
        change(e){
            this.$emit('change',e);
        }
    }
}
</script>

<style scoped lang="less">
    .avatar-border{
        padding: 8px;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        cursor: pointer;
    }
</style>