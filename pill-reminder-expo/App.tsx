import React, { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TouchableOpacity, ScrollView, SafeAreaView, TextInput, Platform, Alert, Image, Modal } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as ImagePicker from 'expo-image-picker';

interface Medication {
  id: string;
  name: string;
  color: string;
  frequency: string;
  customDays?: string[];
  reminderTimes: string[];
  imageUri?: string;
}

export default function App() {
  // Initialize reminder time to 12:00 PM
  const getInitialTime = () => {
    const date = new Date();
    date.setHours(12);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    return date;
  };

  const [currentTime, setCurrentTime] = useState(new Date());
  const [showAddMedication, setShowAddMedication] = useState(false);
  const [selectedColor, setSelectedColor] = useState('#2196F3');
  const [selectedFrequency, setSelectedFrequency] = useState('Daily');
  const [medicationName, setMedicationName] = useState('');
  const [showTimePicker, setShowTimePicker] = useState(false);
  const [customDays, setCustomDays] = useState<string[]>([]);
  const [medications, setMedications] = useState<Medication[]>([]);
  const [activeScreen, setActiveScreen] = useState('Home');
  const [takenReminders, setTakenReminders] = useState<string[]>([]);
  const [editingMedId, setEditingMedId] = useState<string | null>(null);
  const [reminderTimes, setReminderTimes] = useState<string[]>([]);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [selectedHour, setSelectedHour] = useState(12);
  const [selectedMinute, setSelectedMinute] = useState(0);
  const [selectedPeriod, setSelectedPeriod] = useState<'AM' | 'PM'>('PM');

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    loadMedications();
  }, []);

  const loadMedications = async () => {
    try {
      const stored = await AsyncStorage.getItem('medications');
      if (stored) {
        setMedications(JSON.parse(stored));
      }
    } catch (error) {
      console.error('Error loading medications:', error);
    }
  };

  const requestPermissions = async () => {
    const cameraPermission = await ImagePicker.requestCameraPermissionsAsync();
    const mediaPermission = await ImagePicker.requestMediaLibraryPermissionsAsync();
    
    if (cameraPermission.status !== 'granted' || mediaPermission.status !== 'granted') {
      Alert.alert('Permission Required', 'Please grant camera and gallery permissions to add images');
      return false;
    }
    return true;
  };

  const pickImageFromGallery = async () => {
    const hasPermission = await requestPermissions();
    if (!hasPermission) return;

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 0.8,
    });

    if (!result.canceled && result.assets[0]) {
      setSelectedImage(result.assets[0].uri);
    }
  };

  const takePhoto = async () => {
    const hasPermission = await requestPermissions();
    if (!hasPermission) return;

    const result = await ImagePicker.launchCameraAsync({
      allowsEditing: true,
      aspect: [1, 1],
      quality: 0.8,
    });

    if (!result.canceled && result.assets[0]) {
      setSelectedImage(result.assets[0].uri);
    }
  };

  const addReminderTime = () => {
    const timeStr = getCurrentTimeString();
    if (!reminderTimes.includes(timeStr)) {
      setReminderTimes([...reminderTimes, timeStr]);
      // Reset to 12:00 PM for next selection
      setSelectedHour(12);
      setSelectedMinute(0);
      setSelectedPeriod('PM');
    } else {
      Alert.alert('Already Added', 'This time has already been added');
    }
  };

  const removeReminderTime = (time: string) => {
    setReminderTimes(reminderTimes.filter(t => t !== time));
  };

  const saveMedication = async () => {
    if (!medicationName.trim()) {
      Alert.alert('Error', 'Please enter medication name');
      return;
    }

    if (selectedFrequency === 'Custom' && customDays.length === 0) {
      Alert.alert('Error', 'Please select at least one day for custom frequency');
      return;
    }

    if (reminderTimes.length === 0) {
      Alert.alert('Error', 'Please add at least one reminder time');
      return;
    }

    try {
      let updatedMedications;
      
      if (editingMedId) {
        // Update existing medication
        updatedMedications = medications.map(med => 
          med.id === editingMedId 
            ? {
                ...med,
                name: medicationName,
                color: selectedColor,
                frequency: selectedFrequency,
                customDays: selectedFrequency === 'Custom' ? customDays : undefined,
                reminderTimes: reminderTimes,
                imageUri: selectedImage || med.imageUri,
              }
            : med
        );
        Alert.alert('Success', 'Medication updated successfully!');
      } else {
        // Add new medication
        const newMedication: Medication = {
          id: Date.now().toString(),
          name: medicationName,
          color: selectedColor,
          frequency: selectedFrequency,
          customDays: selectedFrequency === 'Custom' ? customDays : undefined,
          reminderTimes: reminderTimes,
          imageUri: selectedImage || undefined,
        };
        updatedMedications = [...medications, newMedication];
        Alert.alert('Success', 'Medication added successfully!');
      }
      
      await AsyncStorage.setItem('medications', JSON.stringify(updatedMedications));
      setMedications(updatedMedications);
      
      // Reset form
      setMedicationName('');
      setSelectedColor('#2196F3');
      setSelectedFrequency('Daily');
      setCustomDays([]);
      setSelectedHour(12);
      setSelectedMinute(0);
      setSelectedPeriod('PM');
      setReminderTimes([]);
      setSelectedImage(null);
      setEditingMedId(null);
      setShowAddMedication(false);
    } catch (error) {
      Alert.alert('Error', 'Failed to save medication');
      console.error('Error saving medication:', error);
    }
  };

  const deleteMedication = async (id: string) => {
    Alert.alert(
      'Delete Medication',
      'Are you sure you want to delete this medication?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: async () => {
            try {
              const updatedMedications = medications.filter(med => med.id !== id);
              await AsyncStorage.setItem('medications', JSON.stringify(updatedMedications));
              setMedications(updatedMedications);
            } catch (error) {
              Alert.alert('Error', 'Failed to delete medication');
            }
          }
        }
      ]
    );
  };

  const editMedication = (med: Medication) => {
    setEditingMedId(med.id);
    setMedicationName(med.name);
    setSelectedColor(med.color);
    setSelectedFrequency(med.frequency);
    setCustomDays(med.customDays || []);
    setReminderTimes(med.reminderTimes);
    setSelectedImage(med.imageUri || null);
    
    // Set time picker to 12:00 PM by default
    setSelectedHour(12);
    setSelectedMinute(0);
    setSelectedPeriod('PM');
    
    setShowAddMedication(true);
  };

  const markAsTaken = (medId: string, time: string) => {
    const key = `${medId}-${time}`;
    if (takenReminders.includes(key)) {
      setTakenReminders(takenReminders.filter(r => r !== key));
    } else {
      setTakenReminders([...takenReminders, key]);
    }
  };

  const toggleCustomDay = (day: string) => {
    if (customDays.includes(day)) {
      setCustomDays(customDays.filter(d => d !== day));
    } else {
      setCustomDays([...customDays, day]);
    }
  };

  const formatDate = (date: Date) => {
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    
    const dayName = days[date.getDay()];
    const monthName = months[date.getMonth()];
    const day = date.getDate();
    const year = date.getFullYear();
    
    let hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'pm' : 'am';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const minutesStr = minutes < 10 ? '0' + minutes : minutes;
    
    return `${dayName}, ${monthName} ${String(day).padStart(2, '0')}, ${year} · ${String(hours).padStart(2, '0')}:${minutesStr} ${ampm}`;
  };

  const getCurrentTimeString = () => {
    const minuteStr = selectedMinute.toString().padStart(2, '0');
    return `${selectedHour}:${minuteStr} ${selectedPeriod}`;
  };

  const confirmTimePicker = () => {
    setShowTimePicker(false);
  };

  const getTodaysReminders = () => {
    const allReminders: Array<{med: Medication, time: string}> = [];
    const now = new Date();
    const currentDay = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][now.getDay()];
    
    medications.forEach((med) => {
      let shouldShow = false;
      
      if (med.frequency === 'Daily') {
        shouldShow = true;
      } else if (med.frequency === 'Weekly') {
        shouldShow = true;
      } else if (med.frequency === 'Monthly') {
        shouldShow = true;
      } else if (med.frequency === 'Custom' && med.customDays) {
        shouldShow = med.customDays.includes(currentDay);
      }
      
      if (shouldShow) {
        med.reminderTimes.forEach((time) => {
          allReminders.push({ med, time });
        });
      }
    });
    
    return allReminders;
  };

  const getUpcomingReminders = () => {
    const todaysReminders = getTodaysReminders();
    
    // Filter out taken reminders
    const upcoming = todaysReminders.filter((reminder) => {
      const key = `${reminder.med.id}-${reminder.time}`;
      return !takenReminders.includes(key);
    });
    
    return upcoming.slice(0, 3);
  };

  const getStatistics = () => {
    const todaysReminders = getTodaysReminders();
    const totalToday = todaysReminders.length;
    
    // Count taken today
    const takenToday = todaysReminders.filter((reminder) => {
      const key = `${reminder.med.id}-${reminder.time}`;
      return takenReminders.includes(key);
    }).length;
    
    // Count pending (not yet taken)
    const pending = totalToday - takenToday;
    
    // Total medications count
    const totalMedications = medications.length;
    
    return {
      takenToday,
      pending,
      total: totalMedications
    };
  };

  const colors = [
    { color: '#2196F3', name: 'Blue' },
    { color: '#F44336', name: 'Red' },
    { color: '#4CAF50', name: 'Green' },
    { color: '#FF9800', name: 'Orange' },
    { color: '#9C27B0', name: 'Purple' },
  ];

  if (showAddMedication) {
    // Add Medication Screen
    return (
      <SafeAreaView style={styles.container}>
        <StatusBar style="dark" />
        <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
          {/* Header */}
          <View style={styles.addMedHeader}>
            <Text style={styles.addMedTitle}>
              {editingMedId ? 'Edit Medication' : 'Add New Medication'}
            </Text>
          </View>

          {/* Medication Name Input */}
          <View style={styles.formSection}>
            <TextInput
              style={styles.input}
              placeholder="Medication Name"
              placeholderTextColor="#999"
              value={medicationName}
              onChangeText={setMedicationName}
            />
          </View>

          {/* Pill Representation Section */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Pill Representation</Text>
            
            {/* Image Preview */}
            {selectedImage && (
              <View style={styles.imagePreviewContainer}>
                <Image source={{ uri: selectedImage }} style={styles.imagePreview} />
                <TouchableOpacity 
                  style={styles.removeImageButton}
                  onPress={() => setSelectedImage(null)}
                >
                  <Text style={styles.removeImageText}>✕ Remove</Text>
                </TouchableOpacity>
              </View>
            )}

            <View style={styles.representationButtons}>
              <TouchableOpacity 
                style={styles.representationButton}
                onPress={takePhoto}
              >
                <Text style={styles.representationIcon}>📷</Text>
                <Text style={styles.representationText}>Take Photo</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.representationButton}
                onPress={pickImageFromGallery}
              >
                <Text style={styles.representationIcon}>🖼️</Text>
                <Text style={styles.representationText}>From Gallery</Text>
              </TouchableOpacity>
            </View>
            
            {/* Color Picker */}
            <Text style={styles.colorPickerLabel}>Or choose a color:</Text>
            <View style={styles.colorPicker}>
              {colors.map((item) => (
                <TouchableOpacity
                  key={item.color}
                  style={[styles.colorButton, { backgroundColor: item.color }]}
                  onPress={() => setSelectedColor(item.color)}
                >
                  {selectedColor === item.color && (
                    <Text style={styles.checkmark}>✓</Text>
                  )}
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Frequency Selection */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Frequency</Text>
            <View style={styles.frequencyButtons}>
              {['Daily', 'Weekly', 'Monthly', 'Custom'].map((freq) => (
                <TouchableOpacity
                  key={freq}
                  style={[
                    styles.frequencyButton,
                    selectedFrequency === freq && styles.frequencyButtonActive
                  ]}
                  onPress={() => setSelectedFrequency(freq)}
                >
                  <Text style={[
                    styles.frequencyText,
                    selectedFrequency === freq && styles.frequencyTextActive
                  ]}>
                    {freq}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Custom Days Selector */}
          {selectedFrequency === 'Custom' && (
            <View style={styles.formSection}>
              <Text style={styles.formLabel}>Select Days</Text>
              <View style={styles.daysContainer}>
                {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day) => (
                  <TouchableOpacity
                    key={day}
                    style={[
                      styles.dayButton,
                      customDays.includes(day) && styles.dayButtonActive
                    ]}
                    onPress={() => toggleCustomDay(day)}
                  >
                    <Text style={[
                      styles.dayText,
                      customDays.includes(day) && styles.dayTextActive
                    ]}>
                      {day}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>
          )}

          {/* Reminder Times */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Reminder Times</Text>
            
            {/* Added Times List */}
            {reminderTimes.length > 0 && (
              <View style={styles.addedTimesList}>
                {reminderTimes.map((time, index) => (
                  <View key={index} style={styles.addedTimeItem}>
                    <Text style={styles.addedTimeText}>⏰ {time}</Text>
                    <TouchableOpacity 
                      style={styles.removeTimeButton}
                      onPress={() => removeReminderTime(time)}
                    >
                      <Text style={styles.removeTimeText}>✕</Text>
                    </TouchableOpacity>
                  </View>
                ))}
              </View>
            )}

            {/* Time Picker */}
            <View>
              <Text style={styles.timePickerLabel}>Select Time:</Text>
              <TouchableOpacity 
                style={styles.timePickerContainer}
                onPress={() => setShowTimePicker(true)}
              >
                <Text style={styles.timePickerIcon}>🕐</Text>
                <View style={styles.timePickerTextContainer}>
                  <Text style={styles.timePickerText}>{getCurrentTimeString()}</Text>
                  <Text style={styles.timePickerHint}>Tap to select different time</Text>
                </View>
              </TouchableOpacity>

              <TouchableOpacity 
                style={styles.addTimeButton}
                onPress={addReminderTime}
              >
                <Text style={styles.addTimeText}>+ Add {getCurrentTimeString()}</Text>
              </TouchableOpacity>
            </View>

            {/* Custom Time Picker Modal */}
            <Modal
              visible={showTimePicker}
              transparent={true}
              animationType="slide"
              onRequestClose={() => setShowTimePicker(false)}
            >
              <View style={styles.modalOverlay}>
                <View style={styles.timePickerModalContent}>
                  <Text style={styles.modalTitle}>Select Time</Text>
                  
                  <View style={styles.timeSelectors}>
                    {/* Hour Selector */}
                    <View style={styles.timeColumn}>
                      <Text style={styles.timeColumnLabel}>Hour</Text>
                      <ScrollView style={styles.timeScroll}>
                        {[...Array(12)].map((_, i) => {
                          const hour = i + 1;
                          return (
                            <TouchableOpacity
                              key={hour}
                              style={[
                                styles.timeOption,
                                selectedHour === hour && styles.timeOptionSelected
                              ]}
                              onPress={() => setSelectedHour(hour)}
                            >
                              <Text style={[
                                styles.timeOptionText,
                                selectedHour === hour && styles.timeOptionTextSelected
                              ]}>
                                {hour}
                              </Text>
                            </TouchableOpacity>
                          );
                        })}
                      </ScrollView>
                    </View>

                    {/* Minute Selector */}
                    <View style={styles.timeColumn}>
                      <Text style={styles.timeColumnLabel}>Minute</Text>
                      <ScrollView style={styles.timeScroll}>
                        {[0, 15, 30, 45].map((minute) => (
                          <TouchableOpacity
                            key={minute}
                            style={[
                              styles.timeOption,
                              selectedMinute === minute && styles.timeOptionSelected
                            ]}
                            onPress={() => setSelectedMinute(minute)}
                          >
                            <Text style={[
                              styles.timeOptionText,
                              selectedMinute === minute && styles.timeOptionTextSelected
                            ]}>
                              {minute.toString().padStart(2, '0')}
                            </Text>
                          </TouchableOpacity>
                        ))}
                      </ScrollView>
                    </View>

                    {/* AM/PM Selector */}
                    <View style={styles.timeColumn}>
                      <Text style={styles.timeColumnLabel}>Period</Text>
                      <View style={styles.periodSelector}>
                        {(['AM', 'PM'] as const).map((period) => (
                          <TouchableOpacity
                            key={period}
                            style={[
                              styles.periodOption,
                              selectedPeriod === period && styles.periodOptionSelected
                            ]}
                            onPress={() => setSelectedPeriod(period)}
                          >
                            <Text style={[
                              styles.periodOptionText,
                              selectedPeriod === period && styles.periodOptionTextSelected
                            ]}>
                              {period}
                            </Text>
                          </TouchableOpacity>
                        ))}
                      </View>
                    </View>
                  </View>

                  <View style={styles.modalButtons}>
                    <TouchableOpacity 
                      style={styles.modalCancelButton}
                      onPress={() => setShowTimePicker(false)}
                    >
                      <Text style={styles.modalCancelText}>Cancel</Text>
                    </TouchableOpacity>
                    <TouchableOpacity 
                      style={styles.modalConfirmButton}
                      onPress={confirmTimePicker}
                    >
                      <Text style={styles.modalConfirmText}>
                        Done ({getCurrentTimeString()})
                      </Text>
                    </TouchableOpacity>
                  </View>
                </View>
              </View>
            </Modal>
          </View>

          <View style={{ height: 120 }} />
        </ScrollView>

        {/* Bottom Action Buttons */}
        <View style={styles.bottomActions}>
          <TouchableOpacity 
            style={styles.cancelButton}
            onPress={() => setShowAddMedication(false)}
          >
            <Text style={styles.cancelButtonText}>Cancel</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={styles.addPillButton}
            onPress={saveMedication}
          >
            <Text style={styles.addPillButtonText}>
              {editingMedId ? 'Update Pill' : 'Add Pill'}
            </Text>
          </TouchableOpacity>
        </View>

        {/* Bottom Navigation Bar */}
        <View style={styles.bottomNav}>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIconActive}>🏠</Text>
            <Text style={styles.navLabelActive}>Home</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>📊</Text>
            <Text style={styles.navLabel}>Stats</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>📅</Text>
            <Text style={styles.navLabel}>Calendar</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>⚙️</Text>
            <Text style={styles.navLabel}>Settings</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  const renderScreen = () => {
    switch (activeScreen) {
      case 'Stats':
        return (
          <View style={styles.screenContainer}>
            <Text style={styles.screenTitle}>📊 Statistics</Text>
            <Text style={styles.screenSubtitle}>Your medication statistics will appear here</Text>
          </View>
        );
      case 'Calendar':
        return (
          <View style={styles.screenContainer}>
            <Text style={styles.screenTitle}>📅 Calendar</Text>
            <Text style={styles.screenSubtitle}>Your medication calendar will appear here</Text>
          </View>
        );
      case 'Settings':
        return (
          <View style={styles.screenContainer}>
            <Text style={styles.screenTitle}>⚙️ Settings</Text>
            <Text style={styles.screenSubtitle}>App settings will appear here</Text>
          </View>
        );
      default:
        return renderHomeScreen();
    }
  };

  const renderHomeScreen = () => {
    return (
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        {/* Top Section - Title Card */}
        <View style={styles.headerCard}>
          <Text style={styles.headerTitle}>Pill Reminder</Text>
          <Text style={styles.headerSubtitle}>Never forget your Medicines</Text>
          <Text style={styles.dateTime}>{formatDate(currentTime)}</Text>
        </View>

        {/* Statistics Section */}
        <View style={styles.statsContainer}>
          <View style={styles.statCard}>
            <Text style={[styles.statNumber, styles.statNumberTaken]}>{getStatistics().takenToday}</Text>
            <Text style={styles.statLabel}>Taken Today</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={[styles.statNumber, styles.statNumberPending]}>{getStatistics().pending}</Text>
            <Text style={styles.statLabel}>Pending</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={[styles.statNumber, styles.statNumberTotal]}>{getStatistics().total}</Text>
            <Text style={styles.statLabel}>Total</Text>
          </View>
        </View>

        {/* Next Reminders Section */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <Text style={styles.iconBell}>🔔</Text>
            <Text style={styles.sectionTitle}>Next Reminders</Text>
          </View>
          {getUpcomingReminders().length === 0 ? (
            <Text style={styles.emptyText}>No upcoming reminders</Text>
          ) : (
            getUpcomingReminders().map((reminder, index) => (
              <View key={index} style={styles.reminderItem}>
                <View style={[styles.reminderColorDot, { backgroundColor: reminder.med.color }]} />
                <View style={styles.reminderInfo}>
                  <Text style={styles.reminderMedName}>
                    {reminder.med.name}
                  </Text>
                  <Text style={styles.reminderTime}>
                    ⏰ {reminder.time}
                  </Text>
                </View>
                <TouchableOpacity 
                  style={styles.takenButton}
                  onPress={() => markAsTaken(reminder.med.id, reminder.time)}
                >
                  <Text style={styles.takenButtonText}>
                    Take
                  </Text>
                </TouchableOpacity>
              </View>
            ))
          )}
        </View>

        {/* My Medication Section */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <Text style={styles.iconBottle}>💊</Text>
            <Text style={styles.sectionTitle}>My Medication</Text>
          </View>
          {medications.length === 0 ? (
            <>
              <Text style={styles.emptyText}>No medications added yet</Text>
              <Text style={styles.hintText}>Add your first medication using the + button above</Text>
            </>
          ) : (
            medications.map((med) => (
              <View key={med.id} style={styles.medicationCard}>
                {med.imageUri ? (
                  <Image source={{ uri: med.imageUri }} style={styles.medImage} />
                ) : (
                  <View style={[styles.medColorIndicator, { backgroundColor: med.color }]} />
                )}
                <View style={styles.medInfo}>
                  <Text style={styles.medName}>{med.name}</Text>
                  <Text style={styles.medDetails}>
                    {med.frequency === 'Custom' 
                      ? `Custom: ${med.customDays?.join(', ')}`
                      : med.frequency
                    }
                  </Text>
                  <Text style={styles.medTime}>⏰ {med.reminderTimes.join(', ')}</Text>
                </View>
                <View style={styles.medActions}>
                  <TouchableOpacity 
                    style={styles.editButton}
                    onPress={() => editMedication(med)}
                  >
                    <Text style={styles.editButtonText}>✏️</Text>
                  </TouchableOpacity>
                  <TouchableOpacity 
                    style={styles.deleteButton}
                    onPress={() => deleteMedication(med.id)}
                  >
                    <Text style={styles.deleteButtonText}>🗑️</Text>
                  </TouchableOpacity>
                </View>
              </View>
            ))
          )}
        </View>

        <View style={{ height: 100 }} />
      </ScrollView>
    );
  };

  // Main Screen
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      
      {renderScreen()}

      {/* Floating Add Button - Only show on Home screen */}
      {activeScreen === 'Home' && (
        <TouchableOpacity 
          style={styles.floatingButton}
          onPress={() => setShowAddMedication(true)}
        >
          <Text style={styles.floatingButtonText}>+</Text>
        </TouchableOpacity>
      )}

      {/* Bottom Navigation Bar */}
      <View style={styles.bottomNav}>
        <TouchableOpacity 
          style={styles.navItem}
          onPress={() => setActiveScreen('Home')}
        >
          <Text style={activeScreen === 'Home' ? styles.navIconActive : styles.navIcon}>🏠</Text>
          <Text style={activeScreen === 'Home' ? styles.navLabelActive : styles.navLabel}>Home</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={styles.navItem}
          onPress={() => setActiveScreen('Stats')}
        >
          <Text style={activeScreen === 'Stats' ? styles.navIconActive : styles.navIcon}>📊</Text>
          <Text style={activeScreen === 'Stats' ? styles.navLabelActive : styles.navLabel}>Stats</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={styles.navItem}
          onPress={() => setActiveScreen('Calendar')}
        >
          <Text style={activeScreen === 'Calendar' ? styles.navIconActive : styles.navIcon}>📅</Text>
          <Text style={activeScreen === 'Calendar' ? styles.navLabelActive : styles.navLabel}>Calendar</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          style={styles.navItem}
          onPress={() => setActiveScreen('Settings')}
        >
          <Text style={activeScreen === 'Settings' ? styles.navIconActive : styles.navIcon}>⚙️</Text>
          <Text style={activeScreen === 'Settings' ? styles.navLabelActive : styles.navLabel}>Settings</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F7FA',
  },
  scrollView: {
    flex: 1,
  },
  headerCard: {
    backgroundColor: '#2196F3',
    padding: 20,
    marginTop: 40,
    marginHorizontal: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  headerSubtitle: {
    fontSize: 16,
    color: '#E3F2FD',
    marginTop: 4,
  },
  dateTime: {
    fontSize: 14,
    color: '#E3F2FD',
    marginTop: 8,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginHorizontal: 16,
    marginTop: 16,
  },
  statCard: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    padding: 16,
    marginHorizontal: 4,
    borderRadius: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  statNumber: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#2196F3',
  },
  statNumberTaken: {
    color: '#4CAF50',
  },
  statNumberPending: {
    color: '#FF9800',
  },
  statNumberTotal: {
    color: '#2196F3',
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 4,
    textAlign: 'center',
  },
  sectionCard: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    marginHorizontal: 16,
    marginTop: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  iconBell: {
    fontSize: 24,
    marginRight: 8,
  },
  iconBottle: {
    fontSize: 24,
    marginRight: 8,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
  },
  emptyText: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
    marginVertical: 8,
  },
  hintText: {
    fontSize: 12,
    color: '#BBB',
    textAlign: 'center',
    marginTop: 4,
  },
  floatingButton: {
    position: 'absolute',
    right: 20,
    bottom: 90,
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: '#2196F3',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 8,
  },
  floatingButtonText: {
    fontSize: 32,
    color: '#FFFFFF',
    fontWeight: '300',
  },
  bottomNav: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    paddingVertical: 8,
    paddingBottom: 12,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: -2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 8,
  },
  navItem: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: 8,
  },
  navIcon: {
    fontSize: 24,
    opacity: 0.5,
  },
  navIconActive: {
    fontSize: 24,
  },
  navLabel: {
    fontSize: 11,
    color: '#999',
    marginTop: 4,
  },
  navLabelActive: {
    fontSize: 11,
    color: '#2196F3',
    marginTop: 4,
    fontWeight: '600',
  },
  // Add Medication Screen Styles
  addMedHeader: {
    paddingHorizontal: 20,
    paddingTop: 40,
    paddingBottom: 10,
  },
  addMedTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#333',
  },
  formSection: {
    paddingHorizontal: 20,
    marginTop: 24,
  },
  formLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 12,
  },
  input: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: '#333',
  },
  representationButtons: {
    flexDirection: 'row',
    gap: 12,
  },
  representationButton: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingVertical: 16,
    paddingHorizontal: 12,
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 8,
  },
  representationIcon: {
    fontSize: 20,
  },
  representationText: {
    fontSize: 14,
    color: '#333',
    fontWeight: '500',
  },
  colorPickerLabel: {
    fontSize: 14,
    color: '#666',
    marginTop: 16,
    marginBottom: 8,
  },
  colorPicker: {
    flexDirection: 'row',
    gap: 12,
    justifyContent: 'center',
  },
  imagePreviewContainer: {
    alignItems: 'center',
    marginBottom: 16,
  },
  imagePreview: {
    width: 150,
    height: 150,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: '#2196F3',
  },
  removeImageButton: {
    marginTop: 8,
    backgroundColor: '#FFEBEE',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
  },
  removeImageText: {
    color: '#C62828',
    fontWeight: '600',
    fontSize: 13,
  },
  colorButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 3,
  },
  checkmark: {
    color: '#FFFFFF',
    fontSize: 24,
    fontWeight: 'bold',
  },
  frequencyButtons: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  frequencyButton: {
    minWidth: '22%',
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 20,
    paddingVertical: 12,
    paddingHorizontal: 8,
    alignItems: 'center',
  },
  frequencyButtonActive: {
    backgroundColor: '#E3F2FD',
    borderColor: '#2196F3',
  },
  frequencyText: {
    fontSize: 14,
    color: '#666',
    fontWeight: '500',
  },
  frequencyTextActive: {
    color: '#2196F3',
    fontWeight: '600',
  },
  timePickerLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
    marginBottom: 8,
  },
  timePickerContainer: {
    backgroundColor: '#FFFFFF',
    borderWidth: 2,
    borderColor: '#2196F3',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 14,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    marginBottom: 12,
  },
  timePickerIcon: {
    fontSize: 24,
  },
  timePickerTextContainer: {
    flex: 1,
  },
  timePickerText: {
    fontSize: 20,
    color: '#2196F3',
    fontWeight: 'bold',
  },
  timePickerHint: {
    fontSize: 11,
    color: '#666',
    marginTop: 2,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  timePickerModalContent: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 20,
    width: '85%',
    maxHeight: '70%',
  },
  modalTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 20,
    textAlign: 'center',
  },
  timeSelectors: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 20,
  },
  timeColumn: {
    flex: 1,
  },
  timeColumnLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
    marginBottom: 8,
    textAlign: 'center',
  },
  timeScroll: {
    maxHeight: 200,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
  },
  timeOption: {
    paddingVertical: 12,
    paddingHorizontal: 8,
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#F0F0F0',
  },
  timeOptionSelected: {
    backgroundColor: '#E3F2FD',
  },
  timeOptionText: {
    fontSize: 18,
    color: '#666',
  },
  timeOptionTextSelected: {
    color: '#2196F3',
    fontWeight: 'bold',
  },
  periodSelector: {
    gap: 8,
  },
  periodOption: {
    paddingVertical: 16,
    paddingHorizontal: 8,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    backgroundColor: '#FFFFFF',
  },
  periodOptionSelected: {
    backgroundColor: '#2196F3',
    borderColor: '#2196F3',
  },
  periodOptionText: {
    fontSize: 18,
    color: '#666',
    fontWeight: '600',
  },
  periodOptionTextSelected: {
    color: '#FFFFFF',
  },
  modalButtons: {
    flexDirection: 'row',
    gap: 12,
  },
  modalCancelButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 8,
    backgroundColor: '#F5F5F5',
    alignItems: 'center',
  },
  modalCancelText: {
    fontSize: 16,
    color: '#666',
    fontWeight: '600',
  },
  modalConfirmButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 8,
    backgroundColor: '#2196F3',
    alignItems: 'center',
  },
  modalConfirmText: {
    fontSize: 16,
    color: '#FFFFFF',
    fontWeight: '600',
  },
  addTimeButton: {
    marginTop: 12,
    backgroundColor: '#E3F2FD',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  addTimeText: {
    fontSize: 14,
    color: '#2196F3',
    fontWeight: '600',
  },
  addedTimesList: {
    marginBottom: 12,
    gap: 8,
  },
  addedTimeItem: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#E8F5E9',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderWidth: 1,
    borderColor: '#4CAF50',
  },
  addedTimeText: {
    fontSize: 15,
    color: '#2E7D32',
    fontWeight: '500',
  },
  removeTimeButton: {
    backgroundColor: '#FFCDD2',
    borderRadius: 12,
    width: 24,
    height: 24,
    justifyContent: 'center',
    alignItems: 'center',
  },
  removeTimeText: {
    fontSize: 14,
    color: '#C62828',
    fontWeight: 'bold',
  },
  bottomActions: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    paddingVertical: 16,
    gap: 12,
    backgroundColor: '#FFFFFF',
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  cancelButton: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  cancelButtonText: {
    fontSize: 16,
    color: '#666',
    fontWeight: '600',
  },
  addPillButton: {
    flex: 1,
    backgroundColor: '#9E9E9E',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  addPillButtonText: {
    fontSize: 16,
    color: '#FFFFFF',
    fontWeight: '600',
  },
  daysContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  dayButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    justifyContent: 'center',
    alignItems: 'center',
  },
  dayButtonActive: {
    backgroundColor: '#2196F3',
    borderColor: '#2196F3',
  },
  dayText: {
    fontSize: 12,
    color: '#666',
    fontWeight: '500',
  },
  dayTextActive: {
    color: '#FFFFFF',
    fontWeight: '600',
  },
  medicationCard: {
    flexDirection: 'row',
    backgroundColor: '#F9FAFB',
    borderRadius: 8,
    padding: 12,
    marginVertical: 6,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  medColorIndicator: {
    width: 4,
    borderRadius: 2,
    marginRight: 12,
  },
  medImage: {
    width: 60,
    height: 60,
    borderRadius: 8,
    marginRight: 12,
  },
  medInfo: {
    flex: 1,
  },
  medName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  medDetails: {
    fontSize: 14,
    color: '#666',
    marginBottom: 2,
  },
  medTime: {
    fontSize: 13,
    color: '#2196F3',
  },
  reminderItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderRadius: 8,
    padding: 12,
    marginVertical: 6,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  reminderColorDot: {
    width: 12,
    height: 12,
    borderRadius: 6,
    marginRight: 12,
  },
  reminderInfo: {
    flex: 1,
  },
  reminderMedName: {
    fontSize: 15,
    fontWeight: '600',
    color: '#333',
    marginBottom: 2,
  },
  reminderTime: {
    fontSize: 13,
    color: '#2196F3',
  },
  takenButton: {
    backgroundColor: '#4CAF50',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 8,
    shadowColor: '#4CAF50',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 3,
    elevation: 3,
  },
  takenButtonText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  medActions: {
    flexDirection: 'row',
    gap: 8,
    alignItems: 'center',
  },
  editButton: {
    backgroundColor: '#E3F2FD',
    borderRadius: 8,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  editButtonText: {
    fontSize: 18,
  },
  deleteButton: {
    backgroundColor: '#FFEBEE',
    borderRadius: 8,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  deleteButtonText: {
    fontSize: 18,
  },
  screenContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  screenTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 12,
  },
  screenSubtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
});
