declare module "@/assets/map.json" {
  type Data = {
    label: string;
    value: string;
    children: Data[];
  };
  const GD_regionData: Data[];
  export default GD_regionData;
}
