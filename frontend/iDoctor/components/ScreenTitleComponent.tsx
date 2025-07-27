import { StyleSheet, Text, View, SafeAreaView } from "react-native";
import FontAwesome from "@expo/vector-icons/FontAwesome";

interface ScreenTitleProps {
  isMainTitle: boolean;
  subtitle?: string;
}

export default function ScreenTitleComponent({
  isMainTitle,
  subtitle,
}: ScreenTitleProps) {
  return (
    <SafeAreaView
      style={{
        justifyContent: "center",
        alignItems: "center",
        marginTop: isMainTitle ? 150 : 40,
      }}
    >
      <View style={{ flexDirection: "row", gap: 10, marginBottom: 10 }}>
        <Text style={{ fontSize: 40, marginLeft: 15 }}>iDoctor</Text>
        <FontAwesome name="stethoscope" size={45} color="black" />
      </View>

      {isMainTitle ? (
        <View style={{ alignItems: "center" }}>
          <Text style={{ fontSize: 16 }}>Know your symptoms</Text>
          <Text style={{ fontSize: 16 }}>find your doctor.</Text>
        </View>
      ) : (
        <Text style={{ fontSize: 16 }}>{subtitle}</Text>
      )}
    </SafeAreaView>
  );
}
