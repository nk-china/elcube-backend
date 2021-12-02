# EAsis 企业级快速开发模型

Easis由easy（简单、轻松）和oasis（绿洲、舒适的地方）两个词组合而成，寓意着企业级的复杂系统的实现可以用很简单的方式，
用户可获得很好的体验，并达成企业的管理目标。同时，也让开发人员更轻松、更从容的应对变更。


### 权利声明
EAsis 遵照AGPL-3.0协议开放源代码，在遵照协议约束条件下，您可以自由传播和修改。
协议原文见LICENSE文件，或查阅 http://www.gnu.org/licenses/agpl-3.0.html

### EAsis是什么

EAsis发源自纽扣互联内部TS5项目，原本是为融资租赁行业行业开发的行业级解决方案，
通过项目实施经验的积累，我们发现TS5是具备一套适应于其他行业的一套通用开发模型。

#### TS5的历史

- 2008年 国内某融资租赁公司的融资租赁系统上线，恰逢当时金融风暴，国内金融行业非常不乐观，
融资租赁行业也受其牵连，央行连续调息、客户逾期不断，给没有经过任何打磨，通过摸石头过河打造的
业务系统带来巨大考验；这套系统是TS5的前身；

- 2009年 第三代融资租赁系统代号T3成功上线，T3系统在经历了前一套系统失败的经验后，重新技术
架构，业务流程，完成了从融资租赁全生命周期流程的建设，完善了资金账务管理及变更体系，打通了
银企直联的账务体系，将资金处理从人工转向自动化，这套系统是TS5的业务雏形；

- 2013年 团队所在的企业全面转向某国际公司大型ERP系统，团队技术转型，成为其ERP系统的实施顾问，
不可否认，国际上领先的思维模式与创新，给团队思想带来了巨大的提升；但国外系统的标准化模块，却因国内
业务的千变万化而水土不服，导致大量功能需要重新定制化开发，在本就不菲的产品价格下，又无形增加了实施的成本；

- 2019年 纽扣互联公司成立，对摆脱企业变更速率与软件跟随曲线的不匹配，导致业务系统实施进入困局，
开始结合十年来积累的经验，重新打造一个渐进化的企业级可配置平台，即TS5

- 2021年 TS5被命名为EAsis，作为开源项目发布

#### 绕不开的 低代码平台

近些年低代码开发平台已经成为行业热词，国内有几十家厂商先后推出了他们的产品，其中不乏阿里、腾讯、华为等大厂。
大部分低代码平台官网上的案例主要为：CRM、报销管理、HR、项目管理等轻应用，大部分平台是以灵活的表单、
流程配置为基础。但流程和表单并不是企业业务的基础。行业内没有专门为企业级应用打造的低代码平台。
一般都是厂商提供的各种行业软件；

为什么没有一个企业级系统没有成功的低代码平台案例呢？我们对这个问题进行深刻思考，“让业务人员自己开发”很难在现阶段达到。
软件工程需要具备结构化思维；

因此，EAsis面向的，是开发人员，而不是面向业务人员的低代码平台。我们定位自己为企业级的软件开发范式。

#### 面向开发人员的快速开发范式，即开发模型

与其他产品不同的是，我们秉承配置即开发的设计理念，将一些开发工作转换成系统配置工作，
通过配置化的方式实现对系统功能的解耦合，以达到快速实施、快速响应变更的目的。

Easis是专门为企业级复杂系统而生的技术平台。它具备低代码开发平台的大部分特性，
但它有别于所有的低代码开发平台。在这个平台上我们不仅给出了一套开箱即用的功能，
甚至还重新定义了软件实施方法。


### 适用场景

#### 业务管理类系统

- 融资租赁、商业保理、供应链金融等泛金融管理系统
- 催收、准入、用户画像等类风控系统
- 仓储、商场后台等类进销存管理系统
- CRM、HR等常见企业系统
- OA的部分场景，不做深入

#### 其他场景

这套架构的能力是足够的，但是是否实际适用，需要各行业专业人士的深度评估后得出结论，
但是我们相信，通过对组件的扩容，它的能力是无限的。

### EAsis 架构介绍

#### 领域建模

单据引擎 是系统核心模块，也是领域建模的基础

通过单据引擎建模，我们可以创建出各类结构化的业务单据，这些单据的结构可以具备深层次结构的

单据由状态、卡片、业务规则、索引、数据同步构成

- 状态：负责控制一个单据的生命周期，即一个业务的生命周期，从创建到审批、直到归档的过程，
不同的状态下，单据可具备不同的业务特性

- 卡片：负责承载业务数据，不同的卡片具备不同的能力及不同的数据结构，完成单据数据的输入与输出

- 业务规则：负责单据内卡片之间的数据交互、不同状态下的数据控制、数据输入时的数据计算逻辑等业务逻辑
甚至可以处理不同的业务单据之间的数据交互

- 索引：单据的数据结构是深层次的，一个单据可能有多个表单，甚至有多个表格，以及嵌套的表格，
通过关系型数据库的表述，就是单据的数据不仅仅只是一个表的一条数据那么简单，因此业务的搜索，是业务系统中
非常重要的一环，索引即是将单据需要检索的字段，通过一定的规则提取，拍扁，形成较为单一，可通过搜索引擎检索的数据格式，
我们通过ElasicSearch完成单据的数据检索工作

- 数据同步：将单据的数据，进行一定格式的数据转换，输出到另一个存储介质，为数据的再利用做准备，如后续的数据报表、
大数据分析、BI智能、风控等；这也是平常系统开发过程中ETL的工作，我们已经提前做好了。

#### 业务流

整个业务系统不仅仅只是对一个表单的输入与输出，而是要负责对整个业务生命周期进行管理。比如一个市场营销的过程，
必然是有 商机->拜访->报价->签约->付款->售后 这样一套业务流程的，甚至业务流程还有分叉，形成一个可以用bpm描述的流程图。

EAsis业务流，是单据引擎的一部分，负责将多个单据，根据业务规则串成一个流程图；

单据负责具体某个业务的具体逻辑，业务流表述具体业务的上下游关系，控制业务从发起到归档的路线。

##### 为什么不用bpm描述业务流？

有些系统的做法，是将整个系统用bpm串起来，完成业务生命周期的管理。

我们不采用bpm来管理业务流的原因，是考虑到业务的原子性，在一个漫长业务流程的业务中，每一个单据都是一个原子性，
每个单据本身就有它的生命周期，而业务本身也具有它的生命周期，这种生命周期的管理，就跟现实一样，层层包裹。
我们需要，当一个单据脱离了业务，它仍然具有生命，是现实中事实存在的一个对象，而不依赖与任何其他因素。

##### 业务流的特性

- 可变化：业务流随时可以根据业务的需求进行调整，而不必担心版本的问题
- 低耦合：业务流的整个大流程是通过两两单据的对应关系，来形成一个业务整体的，任意两个关联的单据的更改，不影响整个业务流
- 多并发循环：业务流中可以支持同时创建多个单据，或递归、循环创建单据，而不存在锁以及上下文污染的问题
- 多终点：业务的特性是从一个业务入口开始，展开业务分支，最终汇入不同的业务终点，业务流完美具备这些特性

#### 审批流

审批流采用bpm，每一个单据都可以配置1个或多个审批流，审批流与业务逻辑隔离。

隔离的好处是职责清晰，单据负责业务的逻辑，bpm负责流程的流转，流程通过控制单据的状态，触发单据的逻辑，来完成整个审批的过程；

基于这些特性，审批流将变得非常简单，甚至不用编码即可完成；


#### 数据检索

得益于单据引擎的强大特性，它已经将需要检索的数据帮我们做好了，数据储存在ElasticSearch搜索引擎里面；

通过配置一个搜索界面，就可以完成一个业务功能的管理。

在这个配置中，支持配置字段、格式、搜索条件、排序等常见的功能；

数据条件包括下拉选项、区间检索、日期检索、搜索提示等搜索条件，支持字段排序、分页，
支持动态汇总下拉菜单的选项，及统计选项下可能的结果集数量；

也可以将搜索条件保存起来，方便下一次快速用同样的条件进行检索；

最重要的是，这些基本不需要任何编码，即可完成千万级数据的秒级响应


#### 数据

#### 权限粒度


### 定制服务
如果您有深度定制服务的需求，请联系 http://www.nkpro.cn/aboutUs?type=ContactUs

### 使用概要
EAsis Backend 为EAsis模型的服务端部分，需要配合EAsis的WEB前端服务一起使用

相关链接：[asdf](docs/UserGuide/index.md)

[EAsis Front Gitee仓库](https://gitee.com/newcorenet/easis-front)

[EAsis Front Github仓库](https://github.com/nk-china/easis-front)

### 软件架构
软件架构说明


### 安装教程

1.  安装JDK
2.  安装关系型数据库，如MySQL5
3.  安装Redis
4.  安装ElasticSearch
5.  在application-dev.yml中配置数据库、Redis、ElasticSearch的地址及密钥
6.  通过SpringBoot启动程序
7.  启动EAsis WEB前端
8.  使用初始管理员admin/admin登陆体验

### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
