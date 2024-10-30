import{p as a,v as o,aT as f}from"./index-DmpP4MtN.js";function v(){const r=a([]),s=a(!0),i=a("no"),e=a("nohide"),p=a("default"),c=a(!1),t=a("right"),h=[{type:"selection",align:"left",reserveSelection:!0,hide:()=>i.value==="no"},{label:"教练姓名",prop:"trainerName",hide:()=>e.value==="hideTrainerName"},{label:"手机号",prop:"numberplate",hide:()=>e.value==="hideNumberplate"},{label:"价格",prop:"trainerPrice",hide:()=>e.value==="hideTrainerPrice"},{label:"体验课数量",prop:"trialClass",hide:()=>e.value==="hideTrialClass"},{label:"正式课数量",prop:"formalClass",hide:()=>e.value==="hideFormalClass"},{label:"总数量",prop:"total",hide:()=>e.value==="hideTotal"}],l=o({pageSize:10,currentPage:1,pageSizes:[10,15,20],total:0,align:"right",background:!0,small:!1}),u=o({text:"正在加载第一页...",viewBox:"-10, -10, 50, 50",spinner:`
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
      `});function d(n){l.small=n}function g(n){}return f(()=>{l.align=t.value}),{loading:s,columns:h,dataList:r,select:i,hideVal:e,tableSize:p,pagination:l,loadingConfig:u,paginationAlign:t,paginationSmall:c,onChange:d,onSizeChange:g}}export{v as useColumns};
