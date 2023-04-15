import React, { useMemo, useRef } from 'react'
import { Animated, NativeSyntheticEvent, ScrollView, StyleSheet, View } from 'react-native'
import { LoremIpsum } from '../components/LoremIpsum'
import BottomSheet, { OffsetChangedEventData } from '../BottomSheet'
import { withNavigationItem } from 'hybrid-navigation'

const HEADER_HEIGTH = 50

const AnimatedBottomSheet = Animated.createAnimatedComponent(BottomSheet)

function BottomSheetBackdropShadow() {
  const offset = useRef(new Animated.Value(0)).current

  const backdropStyle = {
    opacity: offset,
  }

  const onSlide = useMemo(
    () =>
      Animated.event<NativeSyntheticEvent<OffsetChangedEventData>>(
        [
          {
            nativeEvent: {
              offset: offset,
            },
          },
        ],
        { useNativeDriver: true },
      ),
    [offset],
  )

  return (
    <View style={styles.container}>
      <ScrollView>
        <LoremIpsum />
        <LoremIpsum />
        <LoremIpsum />
      </ScrollView>
      <Animated.View
        style={[StyleSheet.absoluteFill, styles.backdrop, backdropStyle]}
        pointerEvents="box-none"
      />
      <AnimatedBottomSheet fitToContents peekHeight={200} onSlide={onSlide}>
        <View style={styles.header} />
        <View style={styles.content}>
          <LoremIpsum words={200} />
        </View>
      </AnimatedBottomSheet>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#eef',
  },
  backdrop: {
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  header: {
    height: HEADER_HEIGTH,
    backgroundColor: 'white',
    paddingHorizontal: 16,
    flexDirection: 'row',
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    elevation: 16,
    shadowRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.4,
    shadowOffset: {
      width: 0,
      height: 0,
    },
  },
  content: {
    backgroundColor: '#ff9f7A',
  },
})

export default withNavigationItem({
  titleItem: {
    title: 'BottomSheet + Backdrop + Shadow',
  },
})(BottomSheetBackdropShadow)
