function delay(n,i){let u=0;return function(){var t=this,e=arguments;clearTimeout(u),u=setTimeout(function(){n.apply(t,e)},i||0)}}