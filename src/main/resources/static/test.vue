<template>
  <view class="container">
    <!-- 标题 -->
    <view class="title">上课预约</view>
    <view class="subtitle">护安行陪驾，开车不再怕！</view>

    <!-- 姓名输入 -->
    <view class="form-item">
      <text class="label">01 姓名</text>
      <input type="text" placeholder="请输入" v-model="name" />
      <view class="desc">请填写真实姓名，用于签署学员免责陪驾协议</view>
    </view>

    <!-- 联系方式 -->
    <view class="form-item">
      <text class="label">02 联系方式</text>
      <input type="number" placeholder="请输入手机号（大陆地区）" v-model="phone" />
    </view>

    <!-- 预约时间 -->
    <view class="form-item">
      <text class="label">03 预约时间</text>
      <picker mode="date" v-model="startTime" @change="onDateChange">
        <view>{{ startTime }}</view>
      </picker>
      <picker mode="date" v-model="endTime" @change="onDateChange">
        <view>{{ endTime }}</view>
      </picker>
      <view>共 {{ getDuration }} 小时</view>
    </view>

    <!-- 接送地址 -->
    <view class="form-item">
      <text class="label">04 接送地址</text>
      <input type="text" placeholder="请输入地址" v-model="address" />
      <view class="desc">小区：XX小区X门 | 单位：XX大厦X门</view>
    </view>

    <!-- 驾驶证上传 -->
    <view class="form-item">
      <text class="label">05 驾驶证</text>
      <button @click="chooseImage">上传图片</button>
      <view v-if="imageSrc">
        <image :src="imageSrc" mode="aspectFit" />
      </view>
      <view class="desc">上传照片或带给教练确认，数量限制：正好1张</view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      name: '',
      phone: '',
      startTime: '',
      endTime: '',
      address: '',
      imageSrc: '',
    };
  },
  computed: {
    getDuration() {
      if (this.startTime && this.endTime) {
        // 时间差计算逻辑
        return 2; // 示例，实际你可以根据时间差计算
      }
      return 0;
    }
  },
  methods: {
    onDateChange(e) {
      // 处理日期选择逻辑
    },
    chooseImage() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.imageSrc = res.tempFilePaths[0];
        }
      });
    }
  }
};
</script>

<style>
.container {
  padding: 20px;
}
.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}
.subtitle {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
}
.form-item {
  margin-bottom: 20px;
}
.label {
  font-size: 16px;
  font-weight: bold;
}
.desc {
  font-size: 12px;
  color: #999;
}
</style>
