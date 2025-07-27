import { ActivityIndicator, View } from "react-native";

export default function LoadingComponent() {
  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <ActivityIndicator size="small" color="black" />
    </View>
  );
}
