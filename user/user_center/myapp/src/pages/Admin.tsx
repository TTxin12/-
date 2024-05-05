import React from 'react';
import {PageContainer} from "@ant-design/pro-components";

const Admin: React.FC = (props) => {
  const {children} = props;
  return (
    <PageContainer>
      {children}
    </PageContainer>
  );
};
export default Admin;
