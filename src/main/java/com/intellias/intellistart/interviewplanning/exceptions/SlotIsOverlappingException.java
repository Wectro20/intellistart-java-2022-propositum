package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception for overlapping time slot.
 */
public class SlotIsOverlappingException extends RuntimeException {

  public SlotIsOverlappingException(long slotId) {
    super("Slot is already exists, id of existing slot: " + slotId);
  }
}
