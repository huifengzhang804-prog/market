declare module "@/assets/json/data.json" {
  type Data = {
    companyName: string;
    companyCode: string;
    companyType: string;
  };
  const receiverCompanyList: Data[];
  export default receiverCompanyList;
}
