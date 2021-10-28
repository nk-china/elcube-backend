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
                <nk-form-item title="类字段" :width="80">
                    <a-input v-model="configEdit.colorField"></a-input>
                </nk-form-item>
                <nk-form-item title="值字段" :width="80">
                    <a-input v-model="configEdit.angleField"></a-input>
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
                        angleField: config.angleField,
                        colorField: config.colorField,
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
                    this.$emit("update",Object.assign({},this.configEdit));
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