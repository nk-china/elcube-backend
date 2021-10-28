<template>
    <nk-meter ref="meter" :title="config.title||title" :editable="editable" :enable-setting="true" @setting-submit="update">
        <div ref="container" style="height: 100%;width: 100%;"></div>
        <div slot="setting">
            <nk-form :col="1" :edit="true">
                <nk-form-item title="标题" :width="80">
                    <a-input slot="edit" v-model="configEdit.title"></a-input>
                </nk-form-item>
                <nk-form-item title="查询语句" :width="80">
                    <a-textarea slot="edit" v-model="configEdit.sql" rows="5"></a-textarea>
                </nk-form-item>
                <nk-form-item title="静态数据" :width="80">
                    <a-textarea slot="edit" v-model="configEdit.data" rows="5"></a-textarea>
                </nk-form-item>
                <nk-form-item title="X轴字段" :width="80">
                    <a-input v-model="configEdit.xField"></a-input>
                </nk-form-item>
                <nk-form-item title="Y轴字段" :width="80">
                    <a-input v-model="configEdit.yFields"></a-input>
                </nk-form-item>
                <nk-form-item title="Y轴别名" :width="80">
                    <a-input v-model="configEdit.yAlias"></a-input>
                </nk-form-item>
                <nk-form-item title="连接区域" :width="80">
                    <a-switch v-model="configEdit.connectedArea"></a-switch>
                </nk-form-item>
                <nk-form-item title="柱形背景" :width="80">
                    <a-switch v-model="configEdit.columnBackground"></a-switch>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>
    import { Column } from '@antv/g2plot';
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

            this.render(this.config);
        },
        methods:{
            load(config){
                return config.sql ?
                    this.$http.postJSON(`/api/dashboard/card/get/${this.$options._componentTag}`,config) :
                    new Promise((r)=>{setTimeout(()=>{r({data:JSON.parse(config.data)});},100);});
            },
            render(config){
                this.load(config).then(res=>{

                    const fields = config.yFields.split(/[,|]/);
                    const alias  = config.yAlias .split(/[,|]/);

                    const data = [];
                    res.data.forEach(i=>{
                        fields.forEach(field=>{
                            data.push({
                                k:i[config.xField],
                                v:i[field],
                                t:alias[fields.indexOf(field)]||field
                            });
                        });
                    });

                    const gConfig = {
                        data : data,
                        isStack: true,
                        xField: 'k',
                        yField: 'v',
                        seriesField: 't',
                        label: {
                            // 可手动配置 label 数据标签位置
                            position: 'middle', // 'top', 'bottom', 'middle',
                            // 配置样式
                            style: {
                                fill: '#FFFFFF',
                                opacity: 0.6,
                            },
                        },
                        xAxis: {
                            label: {
                                autoHide: true,
                                autoRotate: false,
                            },
                        },
                        interactions:  config.connectedArea&&[{ type: 'active-region', enable: false }],
                        connectedArea: config.connectedArea&&{
                            style: (oldStyle) => {
                                return { fill: 'rgba(0,0,0,0.25)', stroke: oldStyle.fill, lineWidth: 0.5 };
                            },
                        },
                        columnBackground: config.columnBackground && !config.connectedArea && {
                            style: {
                                fill: 'rgba(0,0,0,0.1)',
                            },
                        },
                    };

                    if(this.g){
                        this.g.update(gConfig);
                    }else if(this.$refs.container){
                        this.g = new Column(this.$refs.container,gConfig);
                        this.g.render();
                    }
                });
            },
            update(){
                try{
                    this.render(this.configEdit);
                    this.$emit("update",Object.assign({},this.configEdit));
                }catch (e) {
                    console.log(e);
                    this.$refs.meter.setSettingMode(true);
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