<template>
    <nk-meter ref="meter" :title="config.title||title" :editable="editable" :enable-setting="true" @setting-submit="update">
        <div ref="container" style="height: 100%;width: 100%;"></div>
        <div slot="setting">
            <nk-form :col="1" :edit="true">
                <nk-form-item title="标题" :width="80">
                    <a-input slot="edit" v-model="configEdit.title"></a-input>
                </nk-form-item>
                <nk-form-item title="数据" :width="80" v-if="!defaultData">
                    <a-textarea slot="edit" v-model="configEdit.sql" rows="5" placeholder="ElasticSearch SQL OR JSON Data"></a-textarea>
                </nk-form-item>
                <nk-form-item title="X轴字段" :width="80">
                    <a-select v-model="configEdit.xField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="Y轴字段" :width="80">
                    <a-select v-model="configEdit.yFields" :mode="columnDefs?'multiple':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="Y轴别名" :width="80">
                    <a-select v-model="configEdit.yAlias" :mode="'tags'" :options="columnDefs"></a-select>
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
            defaultData:Array,
            columnDefs:Array,
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
            if(this.config.xField&&this.config.yFields){
                this.render(this.config);
            }else{
                this.$refs.meter.setSettingMode(true);
            }
        },
        methods:{
            load(config){
                if(this.defaultData){
                    return Promise.resolve({data:this.defaultData});
                }
                if(config.sql){
                    if(config.sql.trim().startsWith("[") && config.sql.trim().endsWith("]")){
                        return new Promise((r)=>{setTimeout(()=>{r({data:JSON.parse(config.sql)});},100);});
                    }
                    return this.$http.postJSON(`/api/meter/card/get/${this.$options._componentTag}`,config);
                }
                if(config.data){
                    new Promise((r)=>{setTimeout(()=>{r({data:JSON.parse(config.data)});},100);});
                }
                return Promise.resolve({data:[]});
            },
            render(config){
                this.load(config).then(res=>{
                    const fields = config.yFields instanceof Array ? config.yFields : config.yFields.split(/[,|]/);
                    const alias  = config.yAlias instanceof Array  ? config.yAlias  : config.yAlias .split(/[,|]/);

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
                    this.$emit("update:config",Object.assign({},this.configEdit));
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