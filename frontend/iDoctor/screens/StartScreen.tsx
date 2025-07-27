import { Pressable, StyleSheet, Text, View } from "react-native";
import ScreenTitleComponent from "../components/ScreenTitleComponent";
import { NavigationProp, useNavigation } from "@react-navigation/native";
import { RootStackParamList } from "../App";

export default function StartScreen() {
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  return (
    <View style={styles.container}>
      <ScreenTitleComponent isMainTitle />

      <View style={styles.btnContainer}>
        <Pressable
          style={({ pressed }) =>
            pressed ? [styles.pressed, styles.signBtn] : styles.signBtn
          }
          onPress={() => {
            navigation.navigate("SignIn");
          }}
        >
          <Text style={{ color: "white" }}>SIGN IN</Text>
        </Pressable>
        <Pressable
          style={({ pressed }) =>
            pressed ? [styles.pressed, styles.signBtn] : styles.signBtn
          }
          onPress={() => {
            navigation.navigate("UserRegistration");
          }}
        >
          <Text style={{ color: "white" }}>SIGN UP</Text>
        </Pressable>
        <Pressable
          style={({ pressed }) =>
            pressed ? [styles.pressed, styles.signBtn] : styles.signBtn
          }
          onPress={() => {
            navigation.navigate("DoctorRegistration");
          }}
        >
          <Text style={{ color: "white" }}>SIGN UP AS DOCTOR</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "white",
  },
  btnContainer: {
    justifyContent: "center",
    alignItems: "center",
    gap: 25,
    flex: 1,
    marginBottom: 100,
  },

  pressed: {
    opacity: 0.75,
  },

  signBtn: {
    backgroundColor: "black",
    alignItems: "center",
    justifyContent: "center",
    width: "90%",
    borderRadius: 25,
    height: 50,
  },
});
