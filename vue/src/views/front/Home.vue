<template>
  <div class="main-content">
    <div style="display: flex; align-items: flex-start; grid-gap: 10px">

      <div style="width: 150px" class="card">
        <div class="category-item" :class="{ 'category-item-active': item.name === current }"
             v-for="item in categoryList" :key="item.id" @click="selectCategory(item.name)">{{ item.name }}</div>
      </div>

     <div style="flex: 1;" class="card" >
    <blog-list :categoryName="current" />
        <div class="blog-box" v-for="item in tableData" :key="item.id" v-if="total > 0">
          <div style="flex: 1; width: 0">
           <a :href="'/front/blogDetail?blogId='+item.id" target="_blank"><div class="blog-title" >{{ item.title }}</div></a>
          <div class="line1" style="color: #666; margin-bottom: 10px; font-size: 13px">{{ item.descr }}</div>
            <div style="display: flex">
              <div style="flex: 1; font-size: 13px">
                <span style="color: #666; margin-right: 20px"><i class="el-icon-user"></i> {{ item.userName }}</span>
                <span style="color: #666; margin-right: 20px"><i class="el-icon-eye"></i> {{ item.readCount }}</span>
                <span style="color: #666"><i class="el-icon-like"></i> {{ item.likesCount }}</span>
              </div>
              <div style="width: fit-content">
                <el-tag v-for="item in JSON.parse(item.tags || '[]')" :key="item" type="primary" style="margin-right:5px">{{ item }}</el-tag>
              </div>
            </div>
          </div>
          <div style="width: 150px">
            <img style="width: 100%; height: 80px; border-radius: 5px" :src="item.cover" alt="">
          </div>
        </div>

        <div v-if="total === 0" style="padding: 20px 0; text-align: center; font-size: 16px; color: #666">暂无数据</div>

        <div style="margin-top: 10px" v-if="total">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="total">
          </el-pagination>
        </div>

      </div>

      <div style="width: 260px" >
        <div class="card" style="margin-bottom: 10px">
          <div style="font-size: 20px;font-weight: bold;margin-bottom: 10px" >欢迎您 😋</div>
          <a href="/front/person"><div style="color: #666">请写下需要发布的新闻</div></a>
        </div>
        <div style="margin-top: 10px"></div>

        <div class="card">
          <div style="display: flex; align-items: center; padding-bottom: 10px; border-bottom: 1px solid #ddd">
          <div style="font-size: 20px; flex: 1" >新闻榜单</div>
            <div style="font-size: 12px; color: #666; cursor: pointer;" @click="refreshTop"><i class="el-icon-refresh"></i> 换一换</div>
          </div>
          <div>
            <div v-for="item in showList" :key="item.id" style="margin: 15px 0" class="line1">
                      <a :href="'/front/blogDetail?blogId='+item.id" target="_blank">
                        <span style="width: 18px; display: inline-block; text-align: right; margin-right: 10px">
                        <span style="color: orangered" v-if="item.index === 1">{{ item.index }}</span>
                        <span style="color: goldenrod" v-else-if="item.index === 2">{{ item.index }}</span>
                        <span style="color: dodgerblue" v-else-if="item.index === 3">{{ item.index }}</span>
                        <span style="color: #666" v-else>{{ item.index }}</span>
                        </span>
                              <span style="color: #666;">{{ item.title }}</span>
                      </a>
            </div>

          </div>

        </div>
        <div style="margin-bottom: 10px">
          <div v-for="item in topActivityList" :key="item.id" style="margin-bottom: 10px">
            <a :href="'/front/activityDetail?activityId=' + item.id" target="_blank"><img :src="item.cover" alt="" style="width: 100%; border-radius: 5px"></a>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script>

import BlogList from "@/components/BlogList";
export default {
  components: {BlogList},
  data() {
    return {
      current: '全部新闻',  //当前选中的分类名称
      categoryList: [],

      topList: [],
      showList: [],
      topActivityList: [],
      lastIndex:0,

    }
  },
  mounted() {
    this.load()
    this.loadBlogs(1)
    this.refreshTop()
    this.loadTopActivity()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadTopActivity(){
        this.$request.get('/activity/selectTop').then(res => {
          this.topActivityList = res.data || []
        })
    },
    refreshTop(){
      this.$request.get('/blog/selectTop').then(res =>{
        this.topList = res.data||[]
          let i =1;
        this.topList.forEach(item => item.index = i++)

        if (this.lastIndex ===50){
          this.lastIndex =0
        }
        this.showList = this.topList.slice(this.lastIndex,this.lastIndex+5)//从0开始计数，每五个为一个区间//0-5 5-10清零 0-5
        this.lastIndex +=5
      })

    },
    selectCategory(categoryName) {
      this.current = categoryName

    },
    load() {
      // 请求分类的数据
      this.$request.get('/category/selectAll').then(res => {
        this.categoryList = res.data || []
        this.categoryList.unshift({ name: '全部新闻' })
      })
    },


  }
}
</script>

<style>
.category-item {
  text-align: center;
  padding: 10px 0;
  font-size: 16px;
  cursor: pointer;
}
.category-item-active {
  background-color: #1890ff;
  color: #fff;
  border-radius: 5px;
}
.line1 {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

a{
  color: #333;
}
</style>
