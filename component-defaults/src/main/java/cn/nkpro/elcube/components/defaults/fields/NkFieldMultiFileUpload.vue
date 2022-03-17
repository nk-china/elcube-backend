<!--
	This file is part of ELCube.
	ELCube is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCube is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <div style="width: 90%;">
        <div  v-if="!editMode" class="uploads">
            <template v-if="fileList && fileList.length && inputOptions.listType==='picture-card'">
                <div v-for="item in fileList" :key="'key1' + item.uid" class="avatar-border" @click="handlePreview(item)">
                    <a-avatar :src="item.url" shape="square" :size="48"></a-avatar>
                </div>
            </template>
            <template v-else-if="fileList && fileList.length">
                <a v-for="item in fileList" :key="'key2' + item.uid" @click="handlePreview(item)">{{item.name}}</a>
            </template>
            <span v-else class="empty"></span>
        </div>
        <div v-else>
            <a-upload v-if="show && editMode && fileList"
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
                <div>
                    <a-icon :type="progressStatus==='active' ? 'loading' : 'plus'"></a-icon>
                    <div class="ant-upload-text">
                        上传
                    </div>
                </div>
            </a-upload>
        </div>
        <a-progress v-if="editMode && progressPercent!==undefined" :percent="progressPercent" :status="progressStatus" size="small" />
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
        value: [],
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
            show: true,
        }
    },
    watch:{
        value:{
            deep:true,
            handler: function (n,o){
                if(n && (o ? n.length !== o.length : true)){
                    this.show=false;
                    this.$nextTick(()=>{
                        this.nk$editModeChanged();
                        this.show = true;
                    })
                }
            }
        }
    },
    mounted(){
        this.nk$editModeChanged(this.editMode);
    },
    methods:{
        nk$editModeChanged(){
            this.fileList = [];
            if(this.value && this.value.length){
                this.value.forEach(async (valueData,index) =>{
                    let item = {
                        uid: index,
                        name: valueData.name,
                        path: valueData.path,
                        url : undefined,
                    };
                    this.fileList.push(item);
                    if(this.inputOptions.listType==='picture-card'){
                        const res = await this.$http.get("/api/fs/download?url="+valueData.path);
                        item.url = res.data;
                    }
                })
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
                    this.progressStatus  = 'success';
                    this.$message.success(`文件 ${info.file.name} 已上传`);
                    this.fileUploaded(info);
                    break;
                case 'removed':
                    this.fileList = info.fileList;
                    this.$emit('input',this.fileList);
                    break;
            }
        },
        fileUploaded(info){

            info.file.path = info.file.response.url||(this.config.path+(this.config.filename||info.file.name));
            this.fileList.push({
                uid: this.fileList.length ? this.fileList.length + 1 : 0,
                path: info.file.path,
                name: info.file.name,
            });

            this.$emit('input',this.fileList);
            this.change();
        },
        async handlePreview(file) {

            let url = undefined;
            if(!file.path){
                url = await getBase64(file.originFileObj);
            }else{
                const res = await this.$http.get("/api/fs/download?url=" + file.path);
                url = res.data;
            }

            if(url.length<1024*1024){
                if (this.inputOptions.listType === 'picture-card') {
                    this.previewImage = url;
                    this.previewVisible = true;
                } else {
                    this.$refs.link.href=url;
                    this.$refs.link.click();
                }
            }else{
                console.warn("文件URL过大，不能预览",url.length)
                this.$message.warn("文件URL过大，不能预览");
            }
        },
        change(){
            this.$emit('change',[]);
        }
    }
}
</script>
<style scoped>

</style>
<style scoped>
    .avatar-border{
        padding: 8px;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        cursor: pointer;
    }
    .uploads{
        display: flex;
        justify-content: flex-start;
        overflow-y: hidden;
        overflow-x: auto;
    }
</style>