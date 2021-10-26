<template>
    <nk-meter ref="meter" :title="config.title||title" :editable="editable" :enable-setting="true" @setting-submit="update">
        <div ref="container" style="height: 100%;width: 100%;"></div>
        <div slot="setting">
            <nk-form :col="1" :edit="true">
                <nk-form-item title="标题" :width="80">
                    <a-input slot="edit" v-model="configEdit.title"></a-input>
                </nk-form-item>
                <nk-form-item title="数据" :width="80">
                    <a-textarea slot="edit" v-model="configEdit.data" rows="5"></a-textarea>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>
    import { Bar } from '@antv/g2plot';
    export default {
        props:{
            editable:Boolean,
            title:String,
            config:Object,
        },
        data(){
            return {
                g:undefined,
                configEdit:undefined,
            }
        },
        created(){
            this.configEdit = Object.assign({},this.config);
        },
        mounted(){

            if(!this.config.data){
                this.$refs.meter.setSettingMode(true);
                return;
            }

            this.render(this.config)
        },
        methods:{
            render(config){

                new Promise((resolve)=>{
                    setTimeout(()=>{
                        resolve(JSON.parse(config.data));
                    },100);
                }).then((data)=>{
                    const gConfig = {

                        data,
                        xField: 'value',
                        yField: 'label',
                        seriesField: 'label',
                        legend: {
                            position: 'top-left',
                        },
                    };

                    if(this.g){
                        this.g.update(gConfig);
                    }else{
                        this.g = new Bar(this.$refs.container,gConfig);
                        this.g.render();
                    }
                });
            },
            update(){
                try{
                    this.render(this.configEdit);
                    this.$emit("update",Object.assign({},this.configEdit));
                }catch (e) {
                    console.error(e);
                }
            }
        },
        destroyed() {
            if(this.g){
                this.g.destroy();
            }
        }
    }
</script>

<style scoped>

</style>