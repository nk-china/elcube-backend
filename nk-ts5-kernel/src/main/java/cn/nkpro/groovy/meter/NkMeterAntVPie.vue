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
                <nk-form-item title="类字段" :width="80">
                    <a-select v-model="configEdit.colorField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="值字段" :width="80">
                    <a-select v-model="configEdit.angleField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="环比例" :width="80">
                    <a-input-number :min="0.5" :max="0.99" step="0.1" v-model="configEdit.innerRadius"></a-input-number>
                </nk-form-item>
                <nk-form-item title="标签" :width="80">
                    <a-select v-model="configEdit.label" size="small" default-value="inner">
                        <a-select-option key="inner">inner</a-select-option>
                        <a-select-option key="outer">outer</a-select-option>
                        <a-select-option key="spider">spider</a-select-option>
                    </a-select>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>
    import { Pie } from '@antv/g2plot';
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
            if(this.config.colorField&&this.config.angleField){
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

                    let label = undefined;
                    switch (config.label) {
                        case 'outer':
                            label = {
                                type: 'outer',
                                offset: "10px",
                            };
                            break;
                        case 'spider':
                            label = {
                                type: 'spider',
                                offset: "50%",
                                labelHeight: 28,
                                content: ({ percent }) => `${(percent * 100).toFixed(0)}%`,
                            };
                            break;
                        default:
                            label = {
                                type: 'inner',
                                offset: '-50%',
                                content: ({ percent }) => `${(percent * 100).toFixed(0)}%`,
                                style: {
                                    fontSize: 10,
                                    textAlign: 'center',
                                },
                            };
                        break;
                    }

                    const gConfig = {
                        data : res.data,
                        appendPadding: 10,
                        colorField: config.colorField,
                        angleField: config.angleField,
                        radius: 0.9,
                        innerRadius: config.innerRadius,
                        label,
                        interactions: [{ type: 'element-active' }],
                        statistic: {
                            title: false,
                            content: {
                                style: {
                                    whiteSpace: 'pre-wrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                },
                                content: '',
                            },
                        },
                    };

                    if(this.g){
                        this.g.update(gConfig);
                    }else if(this.$refs.container){
                        this.g = new Pie(this.$refs.container,gConfig);
                        this.g.render();
                    }
                });
            },
            update(){
                try{
                    this.render(this.configEdit);
                    this.$emit("update:config",Object.assign({},this.configEdit));
                }catch (e) {
                    console.error(e);
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