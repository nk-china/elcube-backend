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
    import {Column} from '@antv/g2plot';

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
            if(this.configEdit.xField&&this.configEdit.yFields){
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
                    const event = Object.assign({},this.configEdit);
                    this.$emit("update:config",event);
                    this.$emit("input",event);
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