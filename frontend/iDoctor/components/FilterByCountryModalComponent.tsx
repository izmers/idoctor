import { useState } from "react";
import {
  Button,
  FlatList,
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";
import AntDesign from "@expo/vector-icons/AntDesign";

export default function FilterByCountryModalComponent({
  modalVisible,
  handleCloseModal,
  setFilteredCountry,
}: {
  modalVisible: boolean;
  handleCloseModal: () => void;
  setFilteredCountry: (country: string) => void;
}) {
  const allCountries: string[] = [
    "Afghanistan",
    "Albania",
    "Algeria",
    "Andorra",
    "Angola",
    "Antigua and Barbuda",
    "Argentina",
    "Armenia",
    "Australia",
    "Austria",
    "Azerbaijan",
    "Bahamas",
    "Bahrain",
    "Bangladesh",
    "Barbados",
    "Belarus",
    "Belgium",
    "Belize",
    "Benin",
    "Bhutan",
    "Bolivia",
    "Bosnia and Herzegovina",
    "Botswana",
    "Brazil",
    "Brunei",
    "Bulgaria",
    "Burkina Faso",
    "Burundi",
    "Cabo Verde",
    "Cambodia",
    "Cameroon",
    "Canada",
    "Central African Republic",
    "Chad",
    "Chile",
    "China",
    "Colombia",
    "Comoros",
    "Congo (Congo-Brazzaville)",
    "Costa Rica",
    "Croatia",
    "Cuba",
    "Cyprus",
    "Czechia (Czech Republic)",
    "Denmark",
    "Djibouti",
    "Dominica",
    "Dominican Republic",
    "Ecuador",
    "Egypt",
    "El Salvador",
    "Equatorial Guinea",
    "Eritrea",
    "Estonia",
    "Eswatini (fmr. 'Swaziland')",
    "Ethiopia",
    "Fiji",
    "Finland",
    "France",
    "Gabon",
    "Gambia",
    "Georgia",
    "Germany",
    "Ghana",
    "Greece",
    "Grenada",
    "Guatemala",
    "Guinea",
    "Guinea-Bissau",
    "Guyana",
    "Haiti",
    "Honduras",
    "Hungary",
    "Iceland",
    "India",
    "Indonesia",
    "Iran",
    "Iraq",
    "Ireland",
    "Israel",
    "Italy",
    "Jamaica",
    "Japan",
    "Jordan",
    "Kazakhstan",
    "Kenya",
    "Kiribati",
    "Kuwait",
    "Kyrgyzstan",
    "Laos",
    "Latvia",
    "Lebanon",
    "Lesotho",
    "Liberia",
    "Libya",
    "Liechtenstein",
    "Lithuania",
    "Luxembourg",
    "Madagascar",
    "Malawi",
  ];
  const [countries, setCountries] = useState<string[]>(allCountries);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [selectedCountry, setSelectedCountry] = useState<string>("");

  function filterCountry(value: string) {
    setSearchQuery(value);
    const filtered = allCountries.filter((country) =>
      country.toLowerCase().includes(value.toLowerCase())
    );
    setCountries(filtered);
  }

  function handleSelectCountry(country: string) {
    setSelectedCountry((prev) => {
      const newSelection = prev === country ? "" : country;
      setFilteredCountry(newSelection);
      return newSelection;
    });
  }

  return (
    <Modal animationType="slide" transparent={true} visible={modalVisible}>
      <View style={styles.modalContainer}>
        <View style={styles.modalView}>
          <TextInput
            style={{
              width: "90%",
              height: 45,
              padding: 10,
              borderWidth: 1,
              borderRadius: 25,
              marginTop: 15,
            }}
            placeholder="Search for country"
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onChangeText={filterCountry}
            value={searchQuery}
          />

          <FlatList
            style={{ width: "90%", marginTop: 10 }}
            data={countries}
            renderItem={({ item }) => (
              <Pressable
                style={styles.countryBtn}
                onPress={() => handleSelectCountry(item)}
              >
                <Text style={{ fontSize: 20 }}>{item}</Text>
                {selectedCountry === item && (
                  <AntDesign name="checkcircle" size={24} color="black" />
                )}
              </Pressable>
            )}
            keyExtractor={(item) => item}
          />
          <Button title="Close" onPress={handleCloseModal} />
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  modalContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(230, 237, 232, 0.5)",
  },

  modalView: {
    backgroundColor: "white",
    alignItems: "center",
    borderRadius: 15,
    padding: 10,
    width: "90%",
    height: "85%",
  },

  countryBtn: {
    borderBottomWidth: 0.2,
    height: 50,
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row",
  },
});
