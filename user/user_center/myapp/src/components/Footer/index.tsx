import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

//react框架，但是很像js，也是用一个json数组套着
const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'baidu',
          title: 'baidu.com',
          href: 'https://wwww.baidu.com',
          blankTarget: true,
        },
        {
          key: 'yu_que', //在当前部分唯一即可
          title: '笔记文档',
          href: 'https://www.yuque.com/huxin-shcud/ussyr0/mpypcyuumgwpuon2?singleDoc# 《用户中心项目》',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined onPointerEnterCapture={undefined} onPointerLeaveCapture={undefined} /> huxin.Github</>,
          href: 'https://github.com/TTxin12',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
