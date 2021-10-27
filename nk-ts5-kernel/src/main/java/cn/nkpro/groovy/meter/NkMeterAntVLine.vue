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
                    <a-input v-model="configEdit.yField"></a-input>
                </nk-form-item>
                <nk-form-item title="曲线" :width="80">
                    <a-switch v-model="configEdit.smooth" size="small"></a-switch>
                </nk-form-item>
                <nk-form-item title="缩略轴" :width="80">
                    <a-switch v-model="configEdit.slider" size="small"></a-switch>
                </nk-form-item>
                <nk-form-item title="阶梯" :width="80">
                    <a-switch v-model="configEdit.step" size="small"></a-switch>
                </nk-form-item>
                <nk-form-item title="中位数" :width="80">
                    <a-switch v-model="configEdit.median" size="small"></a-switch>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>
    import { Line } from '@antv/g2plot';
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
                    const gConfig = {
                        data : res.data,
                        padding: 'auto',
                        xField: config.xField,
                        yField: config.yField,
                        xAxis: {
                            // type: 'timeCat',
                            tickCount: 5,
                        },
                        smooth: config.smooth,
                        slider: config.slider&&{
                            start: 0,
                            end: 1,
                        },
                        stepType: config.step&&'hvh',
                        annotations: config.median && [
                            {
                                type: 'text',
                                position: ['min', 'median'],
                                content: '中位数',
                                offsetY: -4,
                                style: {
                                    textBaseline: 'bottom',
                                },
                            },
                            {
                                type: 'line',
                                start: ['min', 'median'],
                                end: ['max', 'median'],
                                style: {
                                    stroke: 'red',
                                    lineDash: [2, 2],
                                },
                            },
                        ],
                    };

                    if(this.g){
                        this.g.update(gConfig);
                    }else{
                        this.g = new Line(this.$refs.container,gConfig);
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