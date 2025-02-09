import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Sequence } from '../interfaces/sequence.interface';

@Injectable({
  providedIn: 'root'
})
export class SubmittedSamplesService {
  private submittedSampleSource = new BehaviorSubject<Sequence[]>([]);
  currentlySubmittedSamples = this.submittedSampleSource.asObservable();

  constructor() { }

  changeSubmittedSamples(samples: Sequence[]) {
    this.submittedSampleSource.next(samples);
  }
}
