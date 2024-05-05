import { AvatarDropdown, AvatarName, Footer, Question } from '@/components';
import { currentUser as queryCurrentUser } from '@/services/ant-design-pro/api';
import { LinkOutlined } from '@ant-design/icons';
import type { Settings as LayoutSettings } from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { Link, history } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';
import React from "react";
const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';
const NO_NEED_LOGIN_WHITELIST = ['/user/register',loginPath];

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
/**
 * 初始化时做的事，一些配置，然后加载当前用户信息
 */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
}> {
  //获取当前用户，如果没有就直接跳转到登录页面,每次到一个新页面，都会进行检验
  const fetchUserInfo = async () => {
    try {
      // const msg = await queryCurrentUser({
      //   skipErrorHandler: true,
      // });
      // return msg.data;
      //直接返回用户信息
      return await queryCurrentUser();
    } catch (error) {
      // 进入页面的时候更新一次重定向
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果是登录页面，不执行
  const { location } = history;
  if (location.pathname !== loginPath) {
    return {
      // @ts-ignore
      fetchUserInfo,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  const currentUser = await fetchUserInfo();
  return {
    // @ts-ignore   这里报错是因为当前返回值的类型与上面定义的返回值类型不一样，直接加一个抑制报错的，后边可以来修改这个类型
    fetchUserInfo,
    currentUser,
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  return {
    actionsRender: () => [<Question key="doc" />],
    avatarProps: {
      src: initialState?.currentUser?.avatarUrl,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.userName,
    },
    footerRender: () => <Footer />,
    //更新页面时，也会有一次重定向
    onPageChange: () => {
      const { location } = history;
      //包含在白名单里面的就不用重定向了，意思就是不用用户登录也可以进行操作
      if (NO_NEED_LOGIN_WHITELIST.includes(location.pathname)){
        return;
      }
      // 如果没有登录，重定向到 login，也是对当前用户进行访问
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: isDev
      ? [
          <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
            <LinkOutlined />
            <span>OpenAPI 文档</span>
          </Link>,
        ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * 在这里配置相关的配置信息，比如请求地址前缀之类的
 * @doc https://umijs.org/docs/max/request#配置
 */

// export const request: RequestConfig = {
//   prefix: 'http://localhost:8080',
//   timeout: 1000,
//   ...errorConfig,
// }

export const request = {
  // prefix: 'http://localhost:8080',
  // suffix: '.json',
  timeout: 3000000,
  ...errorConfig
};
