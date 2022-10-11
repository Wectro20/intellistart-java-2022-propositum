package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * The same time slot is already presented exception.
 */
public class SlotIsOverlappingException extends RuntimeException {

  public SlotIsOverlappingException(long slotId) {
    super("Slot is already exists, id of existing slot: " + slotId);
  }
}
