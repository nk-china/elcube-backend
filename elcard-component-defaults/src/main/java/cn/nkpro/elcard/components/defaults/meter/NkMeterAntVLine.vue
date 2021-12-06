<!--
	This file is part of ELCard.
	ELCard is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCard is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <nk-meter ref="meter" :title="configEdit.title||title" :editable="editable" :enable-setting="true" @setting-submit="update">
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
                    <a-select v-model="configEdit.yField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
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
    import {Line} from '@antv/g2plot';

    export default {
        props:{
            editable:Boolean,
            title:String,
            value:Object,
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
            this.configEdit = Object.assign({},this.value);
        },
        mounted(){
            if(this.configEdit.xField&&this.configEdit.yField){
                this.render(this.configEdit);
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
                    }else if(this.$refs.container){
                        this.g = new Line(this.$refs.container,gConfig);
                        this.g.render();
                    }
                });
            },
            update(){
                try{
                    this.render(this.configEdit);
                    const event = Object.assign({},this.configEdit);
                    this.$emit("update:config",event);
                    this.$emit("input",event);
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