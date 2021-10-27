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
                <nk-form-item title="缩略轴" :width="80">
                    <a-switch v-model="configEdit.slider" size="small"></a-switch>
                </nk-form-item>
                <nk-form-item title="中位数" :width="80">
                    <a-switch v-model="configEdit.median" size="small"></a-switch>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>
    import { Waterfall } from '@antv/g2plot';
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
                        appendPadding: [20, 0, 0, 0],
                        xField: config.xField,
                        yField: config.yField,
                        meta: {
                            month: {
                                alias: '月份',
                            },
                            value: {
                                alias: '销售量',
                                formatter: (v) => `${v / 10000000} 亿`,
                            },
                        },
                        /** 展示总计 */
                        total: {
                            label: undefined,
                        },
                        color: ({ month, value }) => {
                            if (month === '2019' || month === '2020' || month==='Total') {
                                return '#96a6a6';
                            }
                            return value > 0 ? '#f4664a' : '#30bf78';
                        },
                        legend: {
                            custom: true,
                            items: [
                                {
                                    name: '增长',
                                    value: 'increase',
                                    marker: { symbol: 'square', style: { r: 5, fill: '#f4664a' } },
                                },
                                {
                                    name: '减少',
                                    value: 'decrease',
                                    marker: { symbol: 'square', style: { r: 5, fill: '#30bf78' } },
                                },
                                {
                                    name: '合计',
                                    value: 'total',
                                    marker: { symbol: 'square', style: { r: 5, fill: '#96a6a6' } },
                                },
                            ],
                        },
                        label: {
                            style: {
                                fontSize: 10,
                            },
                            layout: [{ type: 'interval-adjust-position' }],
                            background: {
                                style: {
                                    fill: '#f6f6f6',
                                    stroke: '#e6e6e6',
                                    strokeOpacity: 0.65,
                                    radius: 2,
                                },
                                padding: 1.5,
                            },
                        },
                        waterfallStyle: () => {
                            return {
                                fillOpacity: 0.85,
                            };
                        },
                    };

                    if(this.g){
                        this.g.update(gConfig);
                    }else{
                        this.g = new Waterfall(this.$refs.container,gConfig);
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